package handler

import (
	"testing"
	"strings"
	"fmt"
	"os/exec"
	"io/ioutil"
)

func TestExeSysCommand(t *testing.T) {
	cmdStr := `iriscli service define --chain-id=irishub-qa --node=35.221.208.174:26657 --from=node0 --fee=0.004iris --service-name=004_service_061222 --tags=a1 a2 a3 a4 a5 --idl-content="syntax = \"proto3\""`
	args := []string {"1234567890"}

	split := strings.Split(cmdStr, " ")

	escape := false
	tempStr := ""
	var cmdArray []string
	for i := 0; i < len(split); i ++ {
		s := split[i]
		if strings.Index(s, "--idl-content") == 0 {
			escape = true
			tempStr = s
			continue
		}

		if escape {
			if strings.Index(s, "--") == 0 {
				cmdArray = append(cmdArray, escapeQuotes(tempStr))
				escape = false
				tempStr = ""
				continue
			}
			tempStr = tempStr + " " + s
		} else {
			cmdArray = append(cmdArray, s)
		}
	}

	if escape {
		cmdArray = append(cmdArray, escapeQuotes(tempStr))
	}

	var cmd *exec.Cmd
	if len(cmdArray) == 1 {
		cmd = exec.Command(cmdArray[0])
	} else {
		cmd = exec.Command(cmdArray[0], cmdArray[1:]...)
	}

	stdin, _ := cmd.StdinPipe()
	stderr, _ := cmd.StderrPipe()
	stdout, _ := cmd.StdoutPipe()

	if err := cmd.Start(); err != nil {
		fmt.Println(err)
	}
	fmt.Printf("Executing command `%v` with arguments: `%v`", cmdStr, args)
	fmt.Printf("Waiting for command to finish...")

	for _, write := range args {
		stdin.Write([]byte(write + "\n"))
	}

	errmsg, _ := ioutil.ReadAll(stderr)
	output, _ := ioutil.ReadAll(stdout)

	if err := cmd.Wait(); err != nil {
		fmt.Printf("Command finished with error: %v, %v", err.Error(), string(errmsg))
		return
	}

	fmt.Printf("Command finished with response: %v", string(output))
}
