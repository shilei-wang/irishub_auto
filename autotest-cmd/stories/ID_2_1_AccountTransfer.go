package stories

import (
	. "bianjie-qa/irishub/autotest-cmd/types"
	. "bianjie-qa/irishub/autotest-cmd/utils"
	"fmt"
)

type AccountTransfer struct {
	Story
	exceptedData [5]*AddAccountResp
	SpecialCaseSet []string
}

func (s *AccountTransfer) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"2-1-1",s.case_2_1_1,UNKNOW,
	"转账交易 - 正常转账 :",""})
}

func (s *AccountTransfer) prepareEnv(subCase *SubCase) error{
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//创建Bruce用户
		resultData, err := s.Common.AddAccount(BRUCE)
		if err != nil {
			return err
		}

		//记录账户数据
		if resultData == nil {}
		//s.exceptedData[0] = resultData
	}

	return nil
}

func (s *AccountTransfer) cleanupEnv(subCase *SubCase){
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//删除Bruce用户
		err := s.Common.DeleteAccount(BRUCE)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		s.exceptedData[0] = nil
	}
}

func (s *AccountTransfer) Run(){
	s.SpecialCaseSet = []string{"2-1-4","2-1-5","2-1-6","2-1-7"}

	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *AccountTransfer) case_2_1_1(subCase *SubCase) (ResultType, string){

	fmt.Println("case_2_1_1")
	return  PASS, ""
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	m1, err := s.Common.GetAccountIris(FAUCET_BANK)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}

	// 执行操作 (从水龙头转账5 iris 给 Bruce)
	_, err = s.Common.SendIris(FAUCET_BANK) 	//sendResp, err := s.Common.SendIris(5, FAUCET_BANK, BRUCE)
	if err != nil {//失败
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_BLOCKTIME) //等待 5s出块时间

	// 获取实际数据
	amount, err := s.Common.GetAccountIris(BRUCE)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据
	err = s.Common.CompareInt64(amount, 5)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	m2, err := s.Common.GetAccountIris(FAUCET_BANK)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	value := m1- m2
	if (value < 5) || (value > 6) {
		return FAIL, ERR_CASE_COMPARE
	}

	return PASS, ""
}

func (s *AccountTransfer) case_2_1_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	return PASS, ""
}
