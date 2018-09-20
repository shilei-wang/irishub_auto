/**************************************
	Story 测试接口:
	1) "POST" http:/ ip:port /gov/proposals
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type SubmitProposal struct {
	Story
	exceptedData [5]string //proposal title
}

func (s *SubmitProposal) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"4-1-1",s.case_4_1_1,UNKNOW,
		"提交提议 - 正常提交 :",""})
}

func (s *SubmitProposal) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "4-1-1" {
		//生成随机数作为 proposal title
		//生成10000-99999区间的5位数随机数
		s.exceptedData[0] = RandomId()
	}

	return nil
}

func (s *SubmitProposal) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "4-1-1"  {
		s.exceptedData[0] = ""
	}
}

func (s *SubmitProposal) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *SubmitProposal) case_4_1_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (FAUCET_GOV 提交 ParameterChange 提议， 抵押100iris)
	_, err = s.Common.SubmitProposal(FAUCET_GOV, s.exceptedData[0], ProposalTypeText, MinDeposit)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_LONGER)

	// 获取实际数据
	resultData, err := s.Common.QueryProposals()
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	length := len(*resultData)
	if length == 0 {
		return FAIL, ERR_CASE_GETACTUAL + ERR_GOV_ZEROPROPOSAL
	}

	actual := (*resultData)[length-1].Title

	//对比数据 (对比实际数据和期望数据 : 得到最新一条的提议的title， 对比title和提交提议时指定时是否一致)
	err = s.Common.CompareString(actual, s.exceptedData[0])
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}





