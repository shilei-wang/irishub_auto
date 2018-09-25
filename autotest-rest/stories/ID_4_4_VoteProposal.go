/**************************************
	Story 测试接口:
	1) "POST" http:/ ip:port /gov/proposals/{proposalID}/votes/,

	type VoteReq struct { //voteReq
		Base 	BaseReq         `json:"base_req"`
		Voter   string 			`json:"voter"`  //  address of the voter
		Option  string 			`json:"option"` //  option from OptionSet chosen by the voter
	}
**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	"errors"
)

type VoteProposal struct {
	Story
	//exceptedData[0] title
	//exceptedData[1] id
	exceptedData [5]string
}

func (s *VoteProposal) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"4-4-1",s.case_4_4_1,UNKNOW,
		"投票提议 - 正常投票 :",""})
}

func (s *VoteProposal) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "4-4-1" {
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
		s.exceptedData[1] = (*resultData)[length-1].ProposalID
	}

	return nil
}

func (s *VoteProposal) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "4-4-1"  {
		s.exceptedData[0] = ""
		s.exceptedData[1] = ""
	}
}

func (s *VoteProposal) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *VoteProposal) case_4_4_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//执行操作 (FAUCET(validator) 投yes给刚才的提议 id=s.exceptedData[1])
	_, err = s.Common.VoteProposal(FAUCET, s.exceptedData[1], VoteOptionYes)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_LONGER)

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





