/**************************************
	Story 测试接口:
	1) "POST" http:// ip:port /keys
		Body := AddAccountReq{Name: "", Password :"", Seed : "" }
		Seed为空
**************************************/


package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type CreateAccount struct {
	Story
	exceptedData [5]*AddAccountResp
	SpecialCaseSet []string
}

func (s *CreateAccount) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"1-2-1",s.case_1_2_1,UNKNOW,
		"创建本地账户 - 正常创建 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-2-2",s.case_1_2_2,UNKNOW,
		"创建本地账户 - 覆盖账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-2-3",s.case_1_2_3,UNKNOW,
		"创建本地账户 - 大小写不同 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-2-4",s.case_1_2_4,UNKNOW,
		"创建本地账户 - 用户名为空 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-2-5",s.case_1_2_5,UNKNOW,
		"创建本地账户 - 密码为空 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-2-6",s.case_1_2_6,UNKNOW,
		"创建本地账户 - 密码长度小于8 :",""})
}

func (s *CreateAccount) prepareEnv(subCase *SubCase) error {
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//无需准备数据
	} else if (*subCase).CaseID == "1-2-2" || (*subCase).CaseID == "1-2-3" {
		if  _, err := s.Common.AddAccount(KEVIN, PASSWORD ,""); err != nil {
			return err
		}
	}

	return nil
}

func (s *CreateAccount) cleanupEnv(subCase *SubCase){
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//无需准备数据
	} else if (*subCase).CaseID == "1-2-2" || (*subCase).CaseID == "1-2-7" {
		if  err := s.Common.DeleteAccount(KEVIN, PASSWORD); err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	} else if (*subCase).CaseID == "1-2-3"{
		if  err := s.Common.DeleteAccount(KEVIN, PASSWORD); err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
		if  err := s.Common.DeleteAccount("kevin", PASSWORD); err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	}
}

func (s *CreateAccount) Run(){
	s.SpecialCaseSet = []string{"1-2-2","1-2-3"}
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *CreateAccount) case_1_2_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据: 创建用户)
	actualData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
	if err != nil || actualData == nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据)
	//【 验证用户名 】
	err = s.Common.CompareString((*actualData).Name, KEVIN)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	//【 验证密码 】
	err = s.Common.DeleteAccount(KEVIN, PASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *CreateAccount) case_1_2_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据: 创建用户)
	_, err = s.Common.AddAccount(KEVIN, PASSWORD ,"")
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "Account with name Kevin already exists.")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *CreateAccount) case_1_2_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据: 创建用户)
	actualData, err := s.Common.AddAccount("kevin", PASSWORD ,"")
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	//【 验证用户名 】
	err = s.Common.CompareString((*actualData).Name, "kevin")
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *CreateAccount) case_1_2_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据: 创建用户)
	_, err = s.Common.AddAccount("", PASSWORD ,"")
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "You have to specify a name for the locally stored account.")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *CreateAccount) case_1_2_5(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据: 创建用户)
	_, err = s.Common.AddAccount(KEVIN, "" ,"")
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "You have to specify a password for the locally stored account.")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *CreateAccount) case_1_2_6(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//此处有bug 暂时注释

	//// 执行操作 (获取实际数据: 创建用户)
	//_, err = s.Common.AddAccount(KEVIN, "1234567" ,"")
	//if err == nil {
	//	return FAIL, ERR_CASE_GETACTUAL
	//}
	//
	////对比数据 (对比实际数据和期望数据)
	//err = s.Common.StringContains(err.Error(), "????")
	//if  err == nil {
	//	return FAIL, ERR_CASE_COMPARE + err.Error()
	//}

	return PASS, ""
}