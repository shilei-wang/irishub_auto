/**************************************

	Story 测试接口:
	1) "GET" http:/ ip:port /gov/proposals/{proposalID}/votes/{voter address}

**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	"errors"
)

type InquiryVote struct {
	Story
	//exceptedData[0] title
	//exceptedData[1] id
	exceptedData [5]string
}

func (s *InquiryVote) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"4-5-1",s.case_4_5_1,UNKNOW,
		"查询投票 - 正常查询 :",""})
}

func (s *InquiryVote) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "4-5-1" {
		s.exceptedData[0] = RandomId() //随机 title

		//执行操作 (FAUCET_GOV 提交 ParameterChange 提议， 抵押100iris)
		_, err := s.Common.SubmitProposal(FAUCET_GOV, s.exceptedData[0], ProposalTypeSoftwareUpgrade, MinDeposit)
		if err != nil {
			return err
		}
		Sleep(SLEEP_LONGER)

		resultData, err := s.Common.QueryProposals()
		if err != nil { //失败
			return err
		}

		length := len(*resultData)
		if length == 0 {
			return errors.New(ERR_GOV_ZEROPROPOSAL)
		}

		//获取ProposalID
		proposalID := (*resultData)[length-1].ProposalID

		//执行操作 (FAUCET(validator) 投yes给刚才的提议 id=s.exceptedData[1])
		_, err = s.Common.VoteProposal(FAUCET, proposalID, VoteOptionYes)
		if err != nil {
			return err
		}
		Sleep(SLEEP_LONGER)

		s.exceptedData[1] = proposalID
	}

	return nil
}

func (s *InquiryVote) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "4-5-1"  {
		s.exceptedData[0] = ""
		s.exceptedData[1] = ""
	}
}

func (s *InquiryVote) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *InquiryVote) case_4_5_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//获取实际数据
	resultData, err := s.Common.InquiryVote(FAUCET, s.exceptedData[1])
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据 (对比vote option)
	err = s.Common.CompareString(resultData.Option, VoteOptionYes)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}





