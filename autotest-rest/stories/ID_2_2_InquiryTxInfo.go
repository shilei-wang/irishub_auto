/**************************************
	Story 测试接口:
	1) "GET" http:// ip:port "/txs/{hash}",
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type InquiryTxInfo struct {
	Story
	exceptedData [5]*BroadcastTxResp
}

func (s *InquiryTxInfo) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"2-2-1",s.case_2_2_1,UNKNOW,
		"查询交易 - 正常查询 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-2-2",s.case_2_2_2,UNKNOW,
		"查询交易 - 指定错误哈希 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-2-3",s.case_2_2_3,UNKNOW,
		"查询交易 - 不指定哈希 :",""})
}

func (s *InquiryTxInfo) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "2-2-1" {
		//创建Bruce用户
		_, err := s.Common.AddAccount(BRUCE, PASSWORD ,"")
		if err != nil {
			return err
		}

		//从水龙头转账5 iris 给 Bruce
		sendResp, err := s.Common.SendIris(FAUCET_BANK, BRUCE, TxAmount, nil)
		if err != nil {//失败
			return err
		}
		Sleep(SLEEP_BLOCKTIME) //等待 5s出块时间

		s.exceptedData[0] = sendResp
	}

	return nil
}

func (s *InquiryTxInfo) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "2-2-1" {
		//删除Bruce用户
		err := s.Common.DeleteAccount(BRUCE, PASSWORD)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		s.exceptedData[0] = nil
	}
}

func (s *InquiryTxInfo) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *InquiryTxInfo) case_2_2_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	txResp, err := s.Common.QueryTxInfo(s.exceptedData[0].Hash)
	if err != nil {//失败
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比数据 (对比实际数据和期望数据 : 对比交易所在块的区块高度)
	err = s.Common.CompareString(txResp.Height, s.exceptedData[0].Height)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *InquiryTxInfo) case_2_2_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	_, err = s.Common.QueryTxInfo("D8106E5051C1F541C25526951AC4E553157F3A81")
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	s.Common.ResetSID(FAUCET_BANK)
	// 对比(实际和期望)数据
	err = s.Common.StringContains(err.Error(), "Tx (D8106E5051C1F541C25526951AC4E553157F3A81) not found")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *InquiryTxInfo) case_2_2_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据)
	_, err = s.Common.QueryTxInfo("")
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	s.Common.ResetSID(FAUCET_BANK)
	// 对比(实际和期望)数据
	err = s.Common.StringContains(err.Error(), "404 page not found")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}



