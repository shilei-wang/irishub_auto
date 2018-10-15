/**************************************
	Story 测试接口:

**************************************/

package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	"strconv"
)

type AccountTransfer struct {
	Story
	exceptedData [5]*AddAccountResp
	SpecialCaseSet []string
}

func (s *AccountTransfer) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"2-1-1",s.case_2_1_1,UNKNOW,
	"转账交易 - 正常转账 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-1-2",s.case_2_1_2,UNKNOW,
		"转账交易 - 重复转账 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-1-3",s.case_2_1_3,UNKNOW,
		"转账交易 - 余额不足 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-1-4",s.case_2_1_4,UNKNOW,
		"转账交易 - 自己转账给自己 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-1-5",s.case_2_1_5,UNKNOW,
		"转账交易 - 转账 0 iris :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-1-6",s.case_2_1_6,UNKNOW,
		"转账交易 - 转账 -1 iris :",""})
	s.SubCases = append(s.SubCases, &SubCase{"2-1-7",s.case_2_1_7,UNKNOW,
		"转账交易 - 使用错误密码 :",""})
}

func (s *AccountTransfer) prepareEnv(subCase *SubCase) error{
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//创建Bruce用户
		resultData, err := s.Common.AddAccount(BRUCE, PASSWORD ,"")
		if err != nil {
			return err
		}

		//记录账户数据
		s.exceptedData[0] = resultData
	}

	return nil
}

func (s *AccountTransfer) cleanupEnv(subCase *SubCase){
	if !InSet(s.SpecialCaseSet, (*subCase).CaseID) {
		//删除Bruce用户
		err := s.Common.DeleteAccount(BRUCE, PASSWORD)
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
	_, err = s.Common.SendIris(FAUCET_BANK, BRUCE, TxAmount, nil) 	//sendResp, err := s.Common.SendIris(5, FAUCET_BANK, BRUCE)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_BLOCKTIME) //等待 5s出块时间

	// 获取实际数据
	amount, err := s.Common.GetAccountIris(BRUCE)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据
	expected_amount,err := strconv.ParseFloat(TxAmount,64)
	err = s.Common.CompareFloat64(amount, expected_amount)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	m2, err := s.Common.GetAccountIris(FAUCET_BANK)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	value := m1- m2
	if value < expected_amount || value > (expected_amount+1) {
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

	// 执行操作 (重复2次， 从水龙头转账5 iris 给 Bruce)
	for i:=0;i<2;i++{
		_, err = s.Common.SendIris(FAUCET_BANK, BRUCE, TxAmount, nil) 	//sendResp, err := s.Common.SendIris(5, FAUCET_BANK, BRUCE)
		if err != nil {
			return FAIL, ERR_CASE_EXECUTE + err.Error()
		}
	}
	Sleep(SLEEP_BLOCKTIME) //等待 5s出块时间

	// 获取实际数据
	amount, err := s.Common.GetAccountIris(BRUCE)
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比(实际和期望)数据
	expected_amount,err := strconv.ParseFloat(TxAmount,64)
	err = s.Common.CompareFloat64(amount, expected_amount*2)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *AccountTransfer) case_2_1_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	//   1) 从水龙头转账 5 iris 给 Bruce
	_, err = s.Common.SendIris(FAUCET_BANK, BRUCE, TxAmount, nil)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}
	Sleep(SLEEP_BLOCKTIME) //等待 5s出块时间

	//   2) 从Bruce转账 6 iris 给 水龙头 (注意: 因为Bruce每次都会新建， Bruce转账所引起的SID不影响下个case， 如果是FAUCET_BANK则会影响)
	expected,err := strconv.ParseFloat(TxAmount,64)
	expected+=1
	expected_amount := strconv.FormatFloat(expected, 'f', 0, 64)
	_, err = s.Common.SendIris(BRUCE, FAUCET_BANK, expected_amount, &SendIrisData{DEFAULT,"0",DEFAULT,DEFAULT})
	if err == nil {
		// 在余额不足的情况下转账成功，则此case执行失败
		return FAIL, ERR_CASE_EXECUTE
	}

	// 对比(实际和期望)数据 ， 如果返回错误里没有包含 "< 6iris" ，则case失败
	err = s.Common.StringContains(err.Error(), "<")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *AccountTransfer) case_2_1_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	_, err = s.Common.SendIris(FAUCET_BANK, FAUCET_BANK, TxAmount, nil)
	if err != nil {
		return FAIL, ERR_CASE_EXECUTE + err.Error()
	}

	return PASS, ""
}

func (s *AccountTransfer) case_2_1_5(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	//   1) 从水龙头转账 0 iris 给 Bruce
	_, err = s.Common.SendIris(FAUCET_BANK, FAUCET, "0", nil)
	if err == nil {
		return FAIL, ERR_CASE_EXECUTE
	}

	//   2) 交易未成功，SID -1 (后续这块可能会根据是实际情况改动)
	s.Common.ResetSID(FAUCET_BANK)

	// 对比(实际和期望)数据 ， 如果返回错误里没有包含 "0iris" ，则case失败
	err = s.Common.StringContains(err.Error(), "0iris-atto")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *AccountTransfer) case_2_1_6(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	//   1) 从水龙头转账 -1 iris 给 Bruce
	_, err = s.Common.SendIris(FAUCET_BANK, FAUCET, "-1", nil)
	if err == nil {
		return FAIL, ERR_CASE_EXECUTE
	}

	//   2) 交易未成功，SID -1 (后续这块可能会根据是实际情况改动)
	s.Common.ResetSID(FAUCET_BANK)

	// 对比(实际和期望)数据 ， 如果返回错误里没有包含 "-1000000000000000000iris" ，则case失败
	err = s.Common.StringContains(err.Error(),  "coin name is empty")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *AccountTransfer) case_2_1_7(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作
	//   1) 从水龙头转账 5 iris 给 Bruce (使用错误密码)
	_, err = s.Common.SendIris(FAUCET_BANK, FAUCET, TxAmount, &SendIrisData{BADPASSWORD,s.Common.GetSID(FAUCET_BANK),DEFAULT,DEFAULT})
	if err == nil {
		return FAIL, ERR_CASE_EXECUTE
	}

	//   2) 交易未成功，SID -1 (后续这块可能会根据是实际情况改动)
		s.Common.ResetSID(FAUCET_BANK)

	// 对比(实际和期望)数据 ， 如果返回错误里没有包含 "< 5000000000000000000iris" ，则case失败
	err = s.Common.StringContains(err.Error(),  "Ciphertext decryption failed")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

