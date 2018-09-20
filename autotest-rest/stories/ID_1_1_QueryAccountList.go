/**************************************
	Story 测试接口:
	1) "GET" http:// ip:port /keys
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type QueryAccountList struct {
	Story
	exceptedData [5]*AddAccountResp
	SpecialCaseSet []string
}

func (s *QueryAccountList) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"1-1-1",s.case_1_1_1,UNKNOW,
		"本地列表查询 - 正常查询 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-1-2",s.case_1_1_2,UNKNOW,
		"本地列表查询 - 多个账户 :",""})
}

func (s *QueryAccountList) prepareEnv(subCase *SubCase) error {
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//创建Kevin用户
		resultData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
		if err != nil {
			return err
		}

		//记录账户数据
		s.exceptedData[0] = resultData
	} else if (*subCase).CaseID == "1-1-2" {
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

func (s *QueryAccountList) cleanupEnv(subCase *SubCase){
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//删除Kevin用户
		err := s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		s.exceptedData[0] = nil
	} else if (*subCase).CaseID == "1-1-2" {
		if  err := s.Common.DeleteAccount(KEVIN, PASSWORD); err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
		if  err := s.Common.DeleteAccount(KENT, PASSWORD); err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
		if  err := s.Common.DeleteAccount(KATIE, PASSWORD); err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	}
}

func (s *QueryAccountList) Run(){
	s.SpecialCaseSet = []string{"1-1-2"}
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *QueryAccountList) case_1_1_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	keysList, err := s.Common.QueryAccountList()
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}
	actualData := s.findElementByName(s.exceptedData[0].Name, keysList)

	// 对比数据 (对比实际数据和期望数据)
	if actualData == nil {
		return FAIL, ERR_CASE_COMPARE + ERR_KEYS_DATAUNFOUND
	}

	return PASS, ""
}

func (s *QueryAccountList) case_1_1_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	keysList, err := s.Common.QueryAccountList()
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比数据 (对比实际数据和期望数据)
	if  actualData := s.findElementByName(KEVIN, keysList); actualData == nil {
		return FAIL, ERR_CASE_COMPARE + ERR_KEYS_DATAUNFOUND
	}
	if  actualData := s.findElementByName(KENT, keysList); actualData == nil {
		return FAIL, ERR_CASE_COMPARE + ERR_KEYS_DATAUNFOUND
	}
	if  actualData := s.findElementByName(KATIE, keysList); actualData == nil {
		return FAIL, ERR_CASE_COMPARE + ERR_KEYS_DATAUNFOUND
	}

	return PASS, ""
}

func (s *QueryAccountList) findElementByName(name string, actualDataList *[]KeysAccountResp) *KeysAccountResp{
	for _, data := range *actualDataList {
		if data.Name == name {
			return &data
		}
	}

	return nil
}
