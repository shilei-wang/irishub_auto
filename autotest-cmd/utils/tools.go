package utils

import (
	. "github.com/irishub_auto/autotest-cmd/types"
	"fmt"
	"time"
	"strconv"
	"math/rand"
	"strings"
	"errors"
)

//////////////////////
func Debug(msg string, debugType int8){
	if debugType == DEBUG_FAIL {
		fmt.Println("****************>>> FAIL <<<******************")
		fmt.Println(msg)
		fmt.Println("**********************************************")
	} else if debugType == DEBUG_MSG {
		fmt.Println(msg)
	}
}

func Sleep(space int8){
	duration := WaitBlockTime
	if space == SLEEP_LONGER {
		duration = WaitBlockTime + 4
	}

	time.Sleep(time.Duration(duration)*time.Second)
}

//////////////////////
func RandomId() string{
	r := rand.New(rand.NewSource(time.Now().Unix()))
	return strconv.Itoa(r.Intn(89999)+10000)
}

func InSet(array []string, value string) bool {
	for _, item := range array {
		if item == value {
			return true
		}
	}

	return false
}

func ParseAddress(s string) (string, error){
	var begin,end int

	if begin = strings.Index(s, AddressPrefix); begin == 0 {
		return "", errors.New(ERR_PARSE_RESP)
	}

	if end = begin+AddressLen; end>len(s) {
		return "", errors.New(ERR_PARSE_RESP)
	}

	return s[begin : end], nil
}

func ParseAccounts(s string) []string{
	accounts := []string{}

	var begin int
	var end int

	for {
		if begin = strings.Index(s, "\n"); begin < 1 {break}
		if end = len(s); end <= begin {break}
		s = s[begin+1:end]
		if end = strings.Index(s, "\t"); end < 1 {break}
		accounts = append(accounts, s[0:end])
	}

	return accounts
}

func GetJson(s string) string {
	begin := strings.Index(s, "{")
	end   := strings.LastIndex(s, "}")

	if begin == -1 || end == -1 || begin >= end {
		return ""
	}

	return s[begin:end+1]
}

func DecodeAmount(s string) (float64, error) {
	s = strings.Replace(s,"iris","",-1)

	f,err := strconv.ParseFloat(s,64)
	if err != nil {
		return 0, errors.New(ERR_DECODE_AMOUNT)
	}
	return f, nil
}