/**************************************
	Story 测试接口:
	1) "DELETE" http:// ip:port /keys/{name}
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type DeleteAccount struct {
	Story
	exceptedData [5]*AddAccountResp
}

func (s *DeleteAccount) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"1-5-1",s.case_1_5_1,UNKNOW,
		"删除本地账户 - 正常删除 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-5-2",s.case_1_5_2,UNKNOW,
		"删除本地账户 - 删除不存在账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-5-3",s.case_1_5_3,UNKNOW,
		"删除本地账户 - 删除空账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-5-4",s.case_1_5_4,UNKNOW,
		"删除本地账户 - 连续删除本地账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-5-5",s.case_1_5_5,UNKNOW,
		"删除本地账户 - 使用错误密码 :",""})
}

func (s *DeleteAccount) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "1-5-1" || (*subCase).CaseID == "1-5-5" {
		//创建Kevin用户
		resultData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
		if err != nil {
			return err
		}

		//记录账户数据
		s.exceptedData[0] = resultData
	} else if (*subCase).CaseID == "1-5-4"  {
		if  _, err := s.Common.AddAccount(KEVIN, PASSWORD ,""); err != nil {
			return err
		}
		if  _, err := s.Common.AddAccount(KENT, PASSWORD ,""); err != nil {
			return err
		}
		if  _, err := s.Common.AddAccount(KATIE, PASSWORD ,""); err != nil {
			return err
		}
	}

	return nil
}

func (s *DeleteAccount) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "1-5-1" {
		s.exceptedData[0] = nil
	} else if (*subCase).CaseID == "1-5-5" {
		//删除Kevin用户
		err := s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	}
}

func (s *DeleteAccount) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *DeleteAccount) case_1_5_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE
	}

	// 执行操作 (获取实际数据 : 删除用户)
	err = s.Common.DeleteAccount(KEVIN, PASSWORD)
	if err != nil { //失败
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据 : nil表示找到数据， 则表示上次没有删除成功)
	_, err = s.Common.ShowAccountInfo(KEVIN)
	//if err != nil {
	if err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	return PASS, ""
}

func (s *DeleteAccount) case_1_5_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE
	}

	// 执行操作 (获取实际数据 : 删除用户)
	err = s.Common.DeleteAccount(KEVIN, PASSWORD)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "Key Kevin not found")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *DeleteAccount) case_1_5_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE
	}

	// 执行操作 (获取实际数据 : 删除用户)
	err = s.Common.DeleteAccount("", PASSWORD)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "404 page not found")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *DeleteAccount) case_1_5_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE
	}

	// 执行操作 (获取实际数据 : 删除用户)
	if  err := s.Common.DeleteAccount(KEVIN, PASSWORD); err != nil {
		return FAIL, ERR_CASE_EXECUTE
	}
	if  err := s.Common.DeleteAccount(KENT, PASSWORD); err != nil {
		return FAIL, ERR_CASE_EXECUTE
	}
	if  err := s.Common.DeleteAccount(KATIE, PASSWORD); err != nil {
		return FAIL, ERR_CASE_EXECUTE
	}

	return PASS, ""
}

func (s *DeleteAccount) case_1_5_5(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE
	}

	// 执行操作 (获取实际数据 : 删除用户)
	err = s.Common.DeleteAccount(KEVIN, BADPASSWORD)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "Ciphertext decryption failed")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

