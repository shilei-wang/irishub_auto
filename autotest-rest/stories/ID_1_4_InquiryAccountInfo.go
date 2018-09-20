/**************************************
	Story 测试接口:
	1) "GET" http:// ip:port /keys/{name}
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type InquiryAccountInfo struct {
	Story
	exceptedData [5]*AddAccountResp
}

func (s *InquiryAccountInfo) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"1-4-1",s.case_1_4_1,UNKNOW,
		"查看本地账户 - 正常查看 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-4-2",s.case_1_4_2,UNKNOW,
		"查看本地账户 - 查看不存在账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-4-3",s.case_1_4_3,UNKNOW,
		"查看本地账户 - 查看空账户 :",""})
}

func (s *InquiryAccountInfo) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "1-4-1"  {
		//创建Kevin用户
		resultData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
		if err != nil {
			return err
		}

		//记录账户数据
		s.exceptedData[0] = resultData
	}

	return nil
}

func (s *InquiryAccountInfo) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "1-4-1"  {
		//删除Kevin用户
		err := s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		s.exceptedData[0] = nil
	}
}

func (s *InquiryAccountInfo) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *InquiryAccountInfo) case_1_4_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 查询用户)
	actualData, err := s.Common.ShowAccountInfo(KEVIN)
	if err != nil || actualData == nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比数据 (对比实际数据和期望数据 : Pub_key)
	err = s.Common.CompareString((*actualData).Pub_key, (*s.exceptedData[0]).Pub_key)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *InquiryAccountInfo) case_1_4_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 查询用户)
	_, err = s.Common.ShowAccountInfo(KEVIN)
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

func (s *InquiryAccountInfo) case_1_4_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 查询用户)
	_, err = s.Common.ShowAccountInfo("")
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



