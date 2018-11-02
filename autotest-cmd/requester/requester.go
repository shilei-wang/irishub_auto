package requester

import (
	. "github.com/irishub_auto/autotest-cmd/types"
	"os"
	"os/exec"
	"time"
	"bufio"
	"errors"
	"fmt"
	"io"
)

type Requester struct {

}

func (r *Requester)MakeRequest(Command string, Params []string, Inputs []string) (string ,error){
	ch := make(chan CmdResp)
	po := make(chan *os.Process)

	go r.ExecCommand(Command, Params, Inputs, po, ch)
	cmdProcess := <-po
	close(po)

	select {
	case cmdResp := <-ch:
		return cmdResp.msg, cmdResp.err
	case <-time.After(time.Duration(CMD_TIMEOUT)*time.Second):
		cmdProcess.Kill()
		time.Sleep(time.Second)
		return "", errors.New(ERR_CMD_TIMEOUT)
	}
}

func (r *Requester)ExecCommand(Command string, Params []string, Inputs []string,  po chan *os.Process, ch chan CmdResp) {
	defer close(ch)

	cmd := exec.Command(Command, Params...)

	//fmt.Println(cmd.Args)

	respBody  := make([]byte, 10000)
	stdout, _ := cmd.StdoutPipe()
	stderr, _ := cmd.StderrPipe()
	stdin , _ := cmd.StdinPipe()

	cmd.Start()
	po <- cmd.Process

	if Inputs != nil {
		time.Sleep(time.Duration(300)*time.Millisecond)

		//注意：1.password只读一次！！ 2.以"\n"为分隔符读取不同行的数据  3.无需等待一次性输入
		//例子：stdin.Write([]byte("y"+ "\n"+Inputs[1]+ "\n"))
		var Input string
		for _, s := range Inputs{
			Input = Input + s + "\n"
		}
		stdin.Write([]byte(Input))
		//fmt.Println(Input)
	}

	reader := bufio.NewReader(stderr)
	n,_ := reader.Read(respBody)
	if n > 0 {
		cmd.Wait()
		ch <- CmdResp{"", errors.New(string(respBody[0:n-1]))}
		return
	}

	reader = bufio.NewReader(stdout)
	n,_ = reader.Read(respBody)
	if n > 0 {
		cmd.Wait()
		//ch <- CmdResp{string(respBody[0:n-1]), nil}
		ch <- CmdResp{string(respBody[0:n]), nil}
		return
	}

	cmd.Wait()
	ch <- CmdResp{"", errors.New(ERR_CMD_ERROR)}
	return
}

func (r *Requester)ExecStart(commandName string, Params []string, isPrint bool) {
	cmd := exec.Command(commandName, Params...)

	stdout, _ := cmd.StdoutPipe()
	cmd.Start()

	reader := bufio.NewReader(stdout)
	var str = ""
	for {
		line, err2 := reader.ReadString('\n')
		if err2 != nil || io.EOF == err2 {
			break
		}

		str = str + line

		if isPrint {
			fmt.Println(line)
		}
	}
	cmd.Wait()
}

func (r *Requester)ExecNoStdout(commandName string, Params []string, Inputs []string) {
	cmd := exec.Command(commandName, Params...)

	//fmt.Println(cmd.Args)
	stdin , _ := cmd.StdinPipe()

	cmd.Start()

	if Inputs != nil {
		time.Sleep(time.Duration(300)*time.Millisecond)

		var Input string
		for _, s := range Inputs{
			Input = Input + s + "\n"
		}
		stdin.Write([]byte(Input))
	}

	cmd.Wait()
}

type CmdResp struct {
	msg        string
	err		   error
}