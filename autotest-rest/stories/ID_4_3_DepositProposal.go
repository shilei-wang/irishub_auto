/**************************************
	Story 测试接口:
	1) "POST" http:/ ip:port /gov/proposals/{proposal id}/deposits

	type DepositReq struct { //depositReq
		Base       BaseReq        `json:"base_req"`
		Depositer  string `json:"depositer"` // Address of the depositer
		Amount     []Coin      `json:"amount"`    // Coins to add to the proposal's deposit
	}
**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	"errors"
)

type DepositProposal struct {
	Story
	//exceptedData[0] title
	//exceptedData[1] id
	exceptedData [5]string
}

func (s *DepositProposal) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"4-3-1",s.case_4_3_1,UNKNOW,
		"赞助提议 - 正常赞助 :",""})
}

func (s *DepositProposal) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "4-3-1" {
		s.exceptedData[0] = RandomId() //随机 title

		//执行操作 (首先提交一个抵押额度不够的提议， FAUCET_GOV 提交 ParameterChange 提议， 抵押5iris)
		_, err := s.Common.SubmitProposal(FAUCET_GOV, s.exceptedData[0], ProposalTypeSoftwareUpgrade, TxAmount)
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

func (s *DepositProposal) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "4-3-1"  {
		s.exceptedData[0] = ""
		s.exceptedData[1] = ""
	}
}

func (s *DepositProposal) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *DepositProposal) case_4_3_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (FAUCET_GOV 提交 ParameterChange 提议， 抵押100iris)
	_, err = s.Common.DepositProposal(FAUCET_GOV, s.exceptedData[1], MinDeposit)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_LONGER)

	// 获取实际数据
	resultData, err := s.Common.InquiryProposal(s.exceptedData[1])
	if err != nil { //失败
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	actualData := resultData.Status

	//对比数据 (对比实际数据和期望数据 : 对比最新的proposal状态是不是从DepositPeriod更新到了VotingPeriod)
	err = s.Common.CompareString(actualData, "VotingPeriod")
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}





