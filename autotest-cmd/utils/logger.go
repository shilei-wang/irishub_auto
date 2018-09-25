package utils

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-cmd/types"

	"html/template"
	"strings"
	"strconv"
	"sync"
	"errors"
	"fmt"
	"reflect"
	"os"
	"time"
)

var Logger = logger{}

type logger struct {
	// 定义表头
	ResultListHeader  *ResultList
	locker            sync.RWMutex
}

// 单向列表结构
type ResultList struct {
	Next 	*ResultList

	ID 		int
	Body 	*SubCase
}

// 插入执行结果(单向列表插入， ID为排列顺序)
func (l *logger) InsertList(body *SubCase) {
	//加锁
	l.locker.Lock()
	defer l.locker.Unlock()

	insert := &ResultList{nil,l.createID((*body).CaseID), body}

	if l.ResultListHeader == nil {
		l.ResultListHeader = insert
		return
	}

	var cur = l.ResultListHeader
	var pre *ResultList
	for {
		if cur == nil {
			(*pre).Next = insert
			break
		}

		if (*cur).ID < (*insert).ID {
			pre = cur
			cur = (*cur).Next
			continue
		} else {
			if pre == nil {
				(*insert).Next = l.ResultListHeader
				l.ResultListHeader = insert
			}else{
				(*insert).Next = cur
				(*pre).Next = insert
			}

			break
		}
	}
}

// 转换 CaseID
func (l *logger) createID(caseId string) int{
	s := strings.Split(caseId, "-")

	var x,y,z int
	var err error

	x, err = strconv.Atoi(s[0])
	if err != nil {
		panic(err)
	}
	y, err = strconv.Atoi(s[1])
	if err != nil {
		panic(err)
	}
	z, err = strconv.Atoi(s[2])
	if err != nil {
		panic(err)
	}

	if z>99 {
		panic(ERR_LOGGER + ERR_CASENUM_OVERFLOW_Z)
	} else if y>99 {
		panic(ERR_LOGGER + ERR_CASENUM_OVERFLOW_Y)
	}

	return (x*10000+y*100+z)
}

// 遍历ResultList 生成各项数据
func (l *logger) generateData() (map[string]int, map[string]string, map[string]string, error) {
	if l.ResultListHeader == nil {
		return nil, nil, nil, errors.New(ERR_NOCASE_RUNNED)
	}

	var sum_All      = 0
	var sum_Key   	 = 0
	var sum_Bank  	 = 0
	var sum_Stake	 = 0
	var sum_Gov  	 = 0
	var sum_Account  = 0
	var sum_Fee      = 0
	var sum_Iparam   = 0

	var pass_All      = 0
	var pass_Key   	  = 0
	var pass_Bank  	  = 0
	var pass_Stake	  = 0
	var pass_Gov  	  = 0
	var pass_Account  = 0
	var pass_Fee      = 0
	var pass_Iparam   = 0

	var resultMap = map[string]string{}
	var result_line = 0 	//从第10行 i10格开始写
	var resultCell string

	var cur = l.ResultListHeader
	for {
		if cur == nil {
			break
		}

		space := l.getSpace((*(*cur).Body).CaseID)
		pass  := ( (*(*cur).Body).ActualResult == PASS )

		switch {
		// case模块
		case space == SPACE_MODULE_KEYS :
			sum_Key++
			if pass { pass_Key++ }
		case space == SPACE_MODULE_BANK :
			sum_Bank++
			if pass { pass_Bank++ }
		case space == SPACE_MODULE_STAKE :
			sum_Stake++
			if pass { pass_Stake++ }
		case space == SPACE_MODULE_GOV :
			sum_Gov++
			if pass { pass_Gov++ }
		case space == SPACE_MODULE_ACCOUNT :
			sum_Account++
			if pass { pass_Account++ }
		case space == SPACE_MODULE_FEE :
			sum_Fee++
			if pass { pass_Fee++ }
		case space == SPACE_MODULE_IPARAM :
			sum_Iparam++
			if pass { pass_Iparam++ }
		}
		sum_All++
		if pass { pass_All++ }

		// 写入右侧的结果报告(Case ID, Summary, Result, Debug Info)
		resultCell = "A"+strconv.Itoa(result_line)
		resultMap[resultCell] = (*(*cur).Body).CaseID
		resultCell = "B"+strconv.Itoa(result_line)
		resultMap[resultCell] = (*(*cur).Body).Description
		resultCell = "C"+strconv.Itoa(result_line)
		resultMap[resultCell] = string((*(*cur).Body).ActualResult)
		resultCell = "D"+strconv.Itoa(result_line)
		resultMap[resultCell] = (*(*cur).Body).DebugInfo
		result_line++

		cur = (*cur).Next
	}

	countMap := map[string]int{
		"A2": sum_Key, "B2": sum_Bank, "C2": sum_Stake, "D2": sum_Gov, "E2": sum_Account, "F2": sum_Fee, "G2": sum_Iparam,"H2": sum_All,
		"A3": pass_Key, "B3": pass_Bank, "C3": pass_Stake, "D3":  pass_Gov, "E3": pass_Account, "F3": pass_Fee,"G3": pass_Iparam,"H3": pass_All,
		"A4": (sum_Key-pass_Key),  "B4": (sum_Bank-pass_Bank), "C4": (sum_Stake-pass_Stake),  "D4": (sum_Gov-pass_Gov),
		"E4": (sum_Account-pass_Account), "F4": (sum_Fee-pass_Fee), "G4": (sum_Iparam-pass_Iparam), "H4": (sum_All-pass_All),
	}

	rateMap := map[string]string{
		"A5": getPassRate(pass_Key, sum_Key), "B5": getPassRate(pass_Bank, sum_Bank), "C5": getPassRate(pass_Stake, sum_Stake),
		"D5": getPassRate(pass_Gov, sum_Gov), "E5": getPassRate(pass_Account, sum_Account), "F5": getPassRate(pass_Fee, sum_Fee),
		"G5": getPassRate(pass_Iparam, sum_Iparam), "H5": getPassRate(pass_All, sum_All),
	}

	return  countMap, rateMap, resultMap, nil
}

// 生成 HTML 报表
func (l *logger) GenerateHTMLReport(r ReportData) {
	// f : 3.**  , 2 : 保留2位小数
	timeElapsed := strconv.FormatFloat(time.Now().Sub(TIME_START).Seconds(), 'f', 2, 32)

	countMap, rateMap, resultMap, err := l.generateData()
	if err != nil {
		Debug(ERR_LOGGER_HTML+err.Error(), DEBUG_MSG)
	}

	detailMap := map[string]string{
		"A1": r.Version, "B1": r.ChainId, "C1":r.FaucetBalance, "D1": r.TimeNow, "E1": timeElapsed+"s",
	}

	type Result struct {
		CaseID       string
		Description  string
		ActualResult string
		DebugInfo    string
	}

	var index = 0 // generateData中的result_line从10开始
	var indexStr string
	results := make([]Result, 0)

	// results = append(results, Result{"Case ID", "Summary", "Result", "Debug Info"})
	// resultMap是不固定长度， 由于 resultMap 里面是序列的，因此先把同行的数据归到一起
	// 读取map(不固定长度)这个方法比较好
	for {
		indexStr = strconv.Itoa(index)
		if _, ok := resultMap["A"+indexStr]; ok {
			results = append(results,
				Result{resultMap["A"+indexStr],
					resultMap["B"+indexStr],
					resultMap["C"+indexStr],
					resultMap["D"+indexStr]})
			index++
		} else {
			break
		}
	}

	//results : [{1-1-1 本地列表查询 - 正常查询 : PASS } {1-1-2 重复查询(2次,幂等 验证) : PASS } ....]
	// rangeStruct 是处理 results 中元素的函数， 目的是把result的struct 转换为 []interface{}数组
	// range 是遍历， "."指的是参数
	var templateFuncs = template.FuncMap{
		"rangeStruct": func(args interface{}) []interface{} {
			//args  {1-1-1 本地列表查询 - 正常查询 : PASS } => type Result struct
			v := reflect.ValueOf(args)
			if v.Kind() != reflect.Struct {
				return nil
			}

			//v.NumField() == 4
			out := make([]interface{}, v.NumField())
			for i := 0; i < v.NumField(); i++ {
				out[i] = v.Field(i).Interface()
			}

			// out = {1-1-1 本地列表查询 - 正常查询 : PASS }
			return out
		},

		"mod": func(a int) bool {
			if a%2 == 0 {
				return true
			}
			return false
		},
	}

	t := template.New("template.tmpl").Funcs(templateFuncs)
	t, err = t.ParseFiles("./testCaseReport/template/template.tmpl")
	if err != nil {
		Debug(ERR_LOGGER_HTML+err.Error(), DEBUG_MSG)
	}

	f, err := os.Create("./testCaseReport/htmlReport.html")
	value := map[string]interface{}{
		"details": detailMap,
		"counts":  countMap,
		"rates":   rateMap,
		"results": results,
	}

	err = t.Execute(f, value)
	if err != nil {
		Debug(ERR_LOGGER_HTML+err.Error(), DEBUG_MSG)
	}
}

// 计算百分比
func getPassRate(passed int , all int) string {
	if (all == 0) {
		return "0%"
	}

	return fmt.Sprintf("%.1f", float32(passed)*100/float32(all))+"%"
}

// 获取space值
func (l *logger) getSpace(caseId string) int {
	s := strings.Split(caseId, "-")

	var x int
	var err error

	x, err = strconv.Atoi(s[0])
	if err != nil {
		panic(err)
	}

	return x
}
