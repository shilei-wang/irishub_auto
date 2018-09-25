/**************************************
	Story 测试接口:
	1) "GET" http:// ip:port //stake/{delegator address}/delegation/{validator address}
**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
)

type InquiryDelegation struct {
	Story
	exceptedData [5]*BroadcastTxResp
}

func (s *InquiryDelegation) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"3-4-1",s.case_3_4_1,UNKNOW,
		"查询委托信息 - 正常查询 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"3-4-2",s.case_3_4_2,UNKNOW,
		"查询委托信息 - 实际未委托 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"3-4-3",s.case_3_4_3,UNKNOW,
		"查询委托信息 - vadlidator填空地址 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"3-4-4",s.case_3_4_4,UNKNOW,
		"查询委托信息 - 验证人查询自己的抵押 :",""})
}

func (s *InquiryDelegation) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "3-4-1" {
		//FAUCET_STAKE账户委托 5iris 给FAUCET账户(validator)
		resultData, err := s.Common.TxDelegation(TxAmount, FAUCET_STAKE, V1_ADDRESS)
		if err != nil {
			return err
		}
		Sleep(SLEEP_BLOCKTIME)

		s.exceptedData[0] = &(*resultData)
	}

	return nil
}

func (s *InquiryDelegation) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "3-4-1"  {
		s.exceptedData[0] = nil
	}
}

func (s *InquiryDelegation) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *InquiryDelegation) case_3_4_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	actualData, err := s.Common.InquiryDelegation(FAUCET_STAKE, V1_ADDRESS)
	if  err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比数据 (对比实际数据和期望数据)
	shares, err := s.Common.DecodeAmount(actualData.Shares)
	if err != nil && shares > 0 {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *InquiryDelegation) case_3_4_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	_, err = s.Common.InquiryDelegation(FAUCET_BANK, V1_ADDRESS)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	// 对比(实际和期望)数据
	err = s.Common.StringContains(err.Error(), "Status code error")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *InquiryDelegation) case_3_4_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	_, err = s.Common.InquiryDelegation(FAUCET_STAKE, "")
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	// 对比(实际和期望)数据
	err = s.Common.StringContains(err.Error(), "Status code error")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *InquiryDelegation) case_3_4_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	actualData, err := s.Common.InquiryDelegation(VALIDATOR_1, V1_ADDRESS)
	if  err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比数据 (对比实际数据和期望数据)
	shares, err := s.Common.DecodeAmount(actualData.Shares)
	if err != nil && shares > 0 {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}


