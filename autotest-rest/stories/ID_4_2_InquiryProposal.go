/**************************************
	Story 测试接口:
	1) "GET" http:/ ip:port /gov/proposals/{proposal id}

**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	"errors"
)

type InquiryProposal struct {
	Story
	//exceptedData[0] title
	//exceptedData[1] id
	exceptedData [5]string
}

func (s *InquiryProposal) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"4-2-1",s.case_4_2_1,UNKNOW,
		"查询提议 - 正常查询 :",""})
}

func (s *InquiryProposal) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "4-2-1" {
		s.exceptedData[0] = RandomId() //随机 title

		//执行操作 (FAUCET_GOV 提交 ParameterChange 提议， 抵押100iris)
		_, err := s.Common.SubmitProposal(FAUCET_GOV, s.exceptedData[0], ProposalTypeText, MinDeposit)
		if err != nil {
			return err
		}
		Sleep(SLEEP_LONGER)

		resultData, err := s.Common.QueryProposals()
		if err != nil {
			return err
		}

		length := len(*resultData)
		if length == 0 {
			return errors.New(ERR_GOV_ZEROPROPOSAL)
		}

		//获取ProposalID
		s.exceptedData[1] = (*resultData)[length-1].ProposalID
	}

	return nil
}

func (s *InquiryProposal) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "4-2-1"  {
		s.exceptedData[0] = ""
		s.exceptedData[1] = ""
	}
}

func (s *InquiryProposal) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *InquiryProposal) case_4_2_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	resultData, err := s.Common.InquiryProposal(s.exceptedData[1])
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	actual := (*resultData).Title

	//对比数据 (对比实际数据和期望数据 : 得到最新一条的提议的title， 对比title和提交提议时指定时是否一致)
	err = s.Common.CompareString(actual, s.exceptedData[0])
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}





