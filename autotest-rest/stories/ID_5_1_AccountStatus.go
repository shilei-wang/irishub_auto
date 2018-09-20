/**************************************
	Story 测试接口:
	1) "GET" http:// ip:port /accounts/{dstResp.Address}
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
	"strconv"
)

type AccountStatus struct {
	Story
	exceptedData [5]*AddAccountResp
}

func (s *AccountStatus) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"5-1-1",s.case_5_1_1,UNKNOW,
		"账户状态查询 - 正常查询 :",""})
}

func (s *AccountStatus) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "5-1-1" {
		//创建Anthony用户
		resultData, err := s.Common.AddAccount(ANTHONY, PASSWORD ,"")
		if err != nil {
			return err
		}

		s.exceptedData[0] = resultData

		//从水龙头转账5 iris 给 Anthony
		_, err = s.Common.SendIris(FAUCET_ACCOUNT, ANTHONY, TxAmount, nil)
		if err != nil {//失败
			return err
		}
		Sleep(SLEEP_BLOCKTIME)
	}

	return nil
}

func (s *AccountStatus) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "5-1-1" {
		//删除Anthony用户
		err := s.Common.DeleteAccount(ANTHONY, PASSWORD)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		s.exceptedData[0] = nil
	}
}

func (s *AccountStatus) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *AccountStatus) case_5_1_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	accountResp, err := s.Common.GetAccountFromAddress(s.exceptedData[0].Address)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 获取实际数据 获取账户余额
	amount, err := s.Common.DecodeAmount(accountResp.Coins[0])
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据
	expected_amount,err := strconv.ParseFloat(TxAmount,64)
	err = s.Common.CompareFloat64(amount , expected_amount)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}




