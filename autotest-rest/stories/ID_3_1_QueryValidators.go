/**************************************
	Story 测试接口:
	1) "GET" http:// ip:port /stake/validators
**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
)

type QueryValidators struct {
	Story
	exceptedData [5]*ValidatorResp
}

func (s *QueryValidators) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"3-1-1",s.case_3_1_1,UNKNOW,
		"验证人列表查询 - 正常查询 :",""})
}

func (s *QueryValidators) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "3-1-1" {
		//查询validator列表无需准备数据
	}

	return nil
}

func (s *QueryValidators) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "3-1-1"  {
		//查询validator列表无需清理数据
	}
}

func (s *QueryValidators) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *QueryValidators) case_3_1_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		//无需期望数据
	}

	// 执行操作 (获取实际数据)
	actualData, err := s.Common.QueryValidatorList()
	if  err != nil {  	//失败
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据)
	if len(*actualData) == 0 {         		//失败
		return FAIL, ERR_CASE_COMPARE + ERR_VALIDATOR_ZERO
	}

	return PASS, ""
}



