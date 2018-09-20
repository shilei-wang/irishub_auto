/**************************************
	Story 测试接口:
	1) "POST" http:// ip:port /keys
		Body := AddAccountReq{Name: "", Password :"", Seed : "助记词" }
		Seed不为空
**************************************/


package stories

import (
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
)

type RecoverAccount struct {
	Story
	exceptedData [5]*AddAccountResp
}

func (s *RecoverAccount) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"1-3-1",s.case_1_3_1,UNKNOW,
		"恢复本地账户 - 本地不存在账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-3-2",s.case_1_3_2,UNKNOW,
		"恢复本地账户 - 本地已存账户 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-3-3",s.case_1_3_3,UNKNOW,
		"恢复本地账户 - 用不同用户名恢复 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-3-4",s.case_1_3_4,UNKNOW,
		"恢复本地账户 - 更换助记词单词 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-3-5",s.case_1_3_5,UNKNOW,
		"恢复本地账户 - 增加助记词单词 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-3-6",s.case_1_3_6,UNKNOW,
		"恢复本地账户 - 减少助记词单词 :",""})
	s.SubCases = append(s.SubCases, &SubCase{"1-3-7",s.case_1_3_7,UNKNOW,
		"恢复本地账户 - 助记词间隔改为2个空格 :",""})
}

func (s *RecoverAccount) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "1-3-1"  {
		//创建Kevin用户
		resultData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
		if err != nil {
			return err
		}

		//记录账户数据
		s.exceptedData[0] = resultData

		//删除Kevin用户
		err = s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {
			return err
		}
	} else if (*subCase).CaseID == "1-3-2" || (*subCase).CaseID == "1-3-3"{
		//创建Kevin用户
		resultData, err := s.Common.AddAccount(KEVIN, PASSWORD ,"")
		if err != nil {
			return err
		}

		//记录账户数据
		s.exceptedData[0] = resultData
	} else {
		// do nothing
	}

	return nil
}

func (s *RecoverAccount) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "1-3-1"  {
		//删除Kevin用户
		err := s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {//失败
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		s.exceptedData[0] = nil
	}

	if (*subCase).CaseID == "1-3-2" {
		err := s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {//失败
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	}

	if (*subCase).CaseID == "1-3-3" {
		err := s.Common.DeleteAccount(KEVIN, PASSWORD)
		if err != nil {//失败
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}

		err = s.Common.DeleteAccount(KENT, PASSWORD)
		if err != nil {//失败
			Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
			return
		}
	}
}

func (s *RecoverAccount) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *RecoverAccount) case_1_3_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	actualData, err := s.Common.AddAccount(KEVIN, PASSWORD ,(*s.exceptedData[0]).Seed)
	if err != nil || actualData == nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比数据 (对比实际数据和期望数据 : Pub_key)
	err = s.Common.CompareString((*actualData).Pub_key, (*s.exceptedData[0]).Pub_key)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *RecoverAccount) case_1_3_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	_, err = s.Common.AddAccount(KEVIN, PASSWORD ,(*s.exceptedData[0]).Seed)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "Account with name Kevin already exists.")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *RecoverAccount) case_1_3_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	actualData, err := s.Common.AddAccount(KENT, PASSWORD ,(*s.exceptedData[0]).Seed)
	if err != nil || actualData == nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	// 对比数据 (对比实际数据和期望数据 : Pub_key)
	err = s.Common.CompareString((*actualData).Pub_key, (*s.exceptedData[0]).Pub_key)
	if err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *RecoverAccount) case_1_3_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//第一个单词由原来的oxygen 换成了apple， apple不在word列表中
	seed := "apple skin spring media trim sphere obtain reason real chapter shoot tennis scan leopard stool bean just demand cliff unusual trust hat vault hazard"

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	_, err = s.Common.AddAccount(KEVIN, PASSWORD ,seed)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "Invalid byte at position")
		if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *RecoverAccount) case_1_3_5(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//第一个单词由原来的oxygen 前增加了一个suit单词
	seed := "suit oxygen skin spring media trim sphere obtain reason real chapter shoot tennis scan leopard stool bean just demand cliff unusual trust hat vault hazard"

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	_, err = s.Common.AddAccount(KEVIN, PASSWORD ,seed)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "recovering only works with 12 word (fundraiser) or 24 word mnemonics")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *RecoverAccount) case_1_3_6(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//删除第一个单词oxygen
	seed := "skin spring media trim sphere obtain reason real chapter shoot tennis scan leopard stool bean just demand cliff unusual trust hat vault hazard"

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	_, err = s.Common.AddAccount(KEVIN, PASSWORD ,seed)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "recovering only works with 12 word (fundraiser) or 24 word mnemonics")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *RecoverAccount) case_1_3_7(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	//删除第一个单词oxygen
	seed := "oxygen  skin  spring  media  trim  sphere  obtain  reason  real  chapter  shoot  tennis  scan  leopard  stool  bean  just  demand  cliff  unusual  trust  hat  vault  hazard"

	// 执行操作 (获取实际数据 : 用刚才获取的seed来恢复用户)
	_, err = s.Common.AddAccount(KEVIN, PASSWORD ,seed)
	if err == nil {
		return FAIL, ERR_CASE_GETACTUAL
	}

	//对比数据 (对比实际数据和期望数据)
	err = s.Common.StringContains(err.Error(), "recovering only works with 12 word (fundraiser) or 24 word mnemonics")
	if  err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}
