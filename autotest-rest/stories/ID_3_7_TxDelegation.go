/**************************************
	Story 测试接口:
	1) "POST" http:/ ip:port /stake/delegations

**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
	"strconv"
)

type TxDelegation struct {
	Story
	exceptedData [5]*InquiryDelegationResp
}

func (s *TxDelegation) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"3-7-1",s.case_3_7_1,UNKNOW,
		"委托交易 - 正常委托 :",""})
}

func (s *TxDelegation) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "3-7-1" {
		_, err := s.Common.TxDelegation(TxAmount, FAUCET_STAKE, V1_ADDRESS)
		if err != nil {
			return err
		}
		Sleep(SLEEP_BLOCKTIME)

		//先查询目前shares的数量
		resultData, err := s.Common.InquiryDelegation(FAUCET_STAKE, V1_ADDRESS)
		if  err != nil {
			return err
		}

		s.exceptedData[0] = resultData
	}

	return nil
}

func (s *TxDelegation) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "3-7-1"  {
		s.exceptedData[0] = nil
	}
}

func (s *TxDelegation) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *TxDelegation) case_3_7_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (FAUCET_STAKE账户委托 5iris 给V1_ADDRESS账户)
	_, err = s.Common.TxDelegation(TxAmount, FAUCET_STAKE, V1_ADDRESS)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_BLOCKTIME)

	// 获取实际数据
	actualData, err := s.Common.InquiryDelegation(FAUCET_STAKE, V1_ADDRESS)
	if  err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	after, _ := s.Common.DecodeAmount((*actualData).Shares)
	before, _ := s.Common.DecodeAmount((*s.exceptedData[0]).Shares)

	//对比(实际和期望)数据
	expected,err := strconv.ParseFloat(TxAmount,64)
	//fmt.Println(after ,before )
	err = s.Common.CompareFloat64((after-before) , expected)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}



