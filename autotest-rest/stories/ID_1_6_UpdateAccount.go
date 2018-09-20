/**************************************
	Story 测试接口:
	1) "PUT" http:// ip:port /keys/{name}
	UpdateKeyReq{NewPassword : NEWPASSWORD, OldPassword :PASSWORD}
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
)

type UpdateAccount struct {
	Story
	exceptedData [5]*AddAccountResp
}

func (s *UpdateAccount) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"1-6-1",s.case_1_6_1,UNKNOW,
		"更新账户密码 - 正常更新 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-6-2",s.case_1_6_2,UNKNOW,
		"更新账户密码 - 更新本地不存在账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-6-3",s.case_1_6_3,UNKNOW,
		"更新账户密码 - 使用相同密码更新 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-6-4",s.case_1_6_4,UNKNOW,
		"更新账户密码 - 连续更新密码 :",""})
}

func (s *UpdateAccount) prepareEnv(subCase *SubCase) error{
	if  (*subCase).CaseID == "1-6-1" || (*subCase).CaseID == "1-6-3" ||
		(*subCase).CaseID == "1-6-4" {
		//创建Kevin用户
		resultData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
		if err != nil {
			return err
		}

		s.exceptedData[0] = resultData
	}

	return nil
}

func (s *UpdateAccount) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "1-6-1" {
		s.exceptedData[0] = nil
	}
}

func (s *UpdateAccount) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *UpdateAccount) case_1_6_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 更新密码用户)
	err = s.Common.UpdateAccount(KEVIN, NEWPASSWORD, PASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据 : 用删除来确认密码有没有更新成功)
	err = s.Common.DeleteAccount(KEVIN, NEWPASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *UpdateAccount) case_1_6_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 更新密码用户)
	err = s.Common.UpdateAccount(KEVIN, NEWPASSWORD, PASSWORD)
	if err == nil {
		return FAIL, ERR_CASE_EXECUTE
	}

	// 对比(实际和期望)数据
	err = s.Common.StringContains(err.Error(), "Key Kevin not found")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *UpdateAccount) case_1_6_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 更新密码用户)
	err = s.Common.UpdateAccount(KEVIN, PASSWORD, PASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据 : 用删除来确认密码有没有更新成功)
	err = s.Common.DeleteAccount(KEVIN, PASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *UpdateAccount) case_1_6_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 更新密码用户)
	err = s.Common.UpdateAccount(KEVIN, NEWPASSWORD, PASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	err = s.Common.UpdateAccount(KEVIN, PASSWORD, NEWPASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据 : 用删除来确认密码有没有更新成功)
	err = s.Common.DeleteAccount(KEVIN, PASSWORD)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}


