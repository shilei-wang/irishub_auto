package utils

import (
	. "bianjie-qa/irishub/autotest-cmd/types"
	"fmt"
	"time"
	"strconv"
	"math/rand"
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