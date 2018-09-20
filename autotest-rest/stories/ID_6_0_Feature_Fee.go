/**************************************
	Story 测试接口:
	1) "POST" http:// ip:port "/accounts/{dstResp.Address}/send",
		sendData := SendData {
			AccountNumber   :  account.AccountNumber,
			Amount          :  []AmountItem{{Amount:c.EncodeAmount(amount), Denom: "iris"}},
			ChainId         :  Config.Map["ChainID"],
			Name            :  srcName,
			Password        :  PASSWORD,
			Sequence        :  account.Sequence,
			Gas             :  "10000",
			Fee             :  c.GetFee(10000),
		}
**************************************/

package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
	//"fmt"
)

type Fee struct {
	Story
}

func (s *Fee) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"6-1-1",s.case_6_1_1,UNKNOW,
		"[新功能] Fee - 正常TX:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-1-2",s.case_6_1_2,UNKNOW,
		"[新功能] Fee - 未指定Fee和Gas:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-1-3",s.case_6_1_3,UNKNOW,
		"[新功能] Fee - 未指定Fee:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-1-4",s.case_6_1_4,UNKNOW,
		"[新功能] Fee - 未指定Gas:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-2-1",s.case_6_2_1,UNKNOW,
		"[新功能] Fee - 非iris token:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-2-2",s.case_6_2_2,UNKNOW,
		"[新功能] Fee - 多token（包含其他coin）:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-2-3",s.case_6_2_3,UNKNOW,
		"[新功能] Fee - 指定不合法的fee:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-3-1",s.case_6_3_1,UNKNOW,
		"[新功能] Fee - gas够 && gasPrice < gasPriceThreshold:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-3-2",s.case_6_3_2,UNKNOW,
		"[新功能] Fee - gas够 && gasPrice == gasPriceThreshold:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-3-3",s.case_6_3_3,UNKNOW,
		"[新功能] Fee - gas不够 && gasPrice > gasPriceThreshold:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-3-4",s.case_6_3_4,UNKNOW,
		"[新功能] Fee - gas不够 && gasPrice < gasPriceThreshold:",""})
	s.SubCases = append(s.SubCases, &SubCase{"6-4-1",s.case_6_4_1,UNKNOW,
		"[新功能] Fee - 余额 < gas * gasPrice + send token:",""})
}

func (s *Fee) prepareEnv(subCase *SubCase) error{
	if  (*subCase).CaseID == "6-4-1" {
		_, err := s.Common.AddAccount(FRANK, PASSWORD ,"")
		if err != nil {
			return err
		}

		_, err = s.Common.SendIris(FAUCET_FEE, FRANK, TxAmount, nil)
		if err != nil {
			return err
		}
		Sleep(SLEEP_BLOCKTIME) //等待 5s出块时间
	}

	return nil
}

func (s *Fee) cleanupEnv(subCase *SubCase){
	if  (*subCase).CaseID == "6-4-1" {
		err := s.Common.DeleteAccount(FRANK, PASSWORD)
		if err != nil {
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	}
}

func (s *Fee) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *Fee) case_6_1_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,GasForFee})
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *Fee) case_6_1_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),"",""})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "unexpected end of JSON input")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_1_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,""})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "Fee required but not specified")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_1_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),"",GasForFee})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "unexpected end of JSON")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_2_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,"0.04iris-iota"})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "convert error")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_2_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,"0.04iris,100neo"})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "can't find any information about coin type")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_2_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,"0.04"})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "can't find any information about coin type")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_3_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,"0.003iris"})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "less than threshold")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_3_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),GasForSend,GasForFee})
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE
	}

	return PASS, ""
}

func (s *Fee) case_6_3_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),"1000","1iris"})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "out of gas in location")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_3_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FAUCET_FEE, FAUCET, TxAmount, &SendIrisData{DEFAULT,s.Common.GetSID(FAUCET_FEE),"1000","0.00001iris"})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "less than threshold")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}

func (s *Fee) case_6_4_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	// 1) 从水龙头转账 1 iris 给 FAUCET 此处单位改了的话 可能要一起改
	_, err = s.Common.SendIris(FRANK, FAUCET, TxAmount, &SendIrisData{DEFAULT,DEFAULT,GasForSend,GasForFee})
	if  err == nil {
		return FAIL, ERR_CASE_COMPARE
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "<")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	s.Common.ResetSID(FAUCET_FEE)
	return PASS, ""
}
