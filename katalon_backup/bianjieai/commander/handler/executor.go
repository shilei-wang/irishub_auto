package handler

import (
	"net/http"
	"os/exec"
	"strings"
	"log"
	"io/ioutil"
)

func ExeSysCommand() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		r.ParseForm()
		cmdStr := r.FormValue("command")
		args := r.Form["args"]
		escapes := r.FormValue("escapes")
		escapeParams := strings.Split(escapes, ",")
		cmd := getCmd(cmdStr, escapeParams)
		stdin, _ := cmd.StdinPipe()
		stderr, _ := cmd.StderrPipe()
		stdout, _ := cmd.StdoutPipe()

		if err := cmd.Start(); err != nil {
			log.Println(err)
		}
		log.Printf("------------------------------------------------------------------------------------------")
		if !strings.Contains(cmdStr, "status") {
			log.Printf("COMMAND: \n `%v + %v`", cmdStr, args)
		}



		for _, write := range args {
			stdin.Write([]byte(write + "\n"))
		}

		errmsg, _ := ioutil.ReadAll(stderr)
		output, _ := ioutil.ReadAll(stdout)

		if err := cmd.Wait(); err != nil {
			log.Printf("Command finished with error:  \n %v, %v", err.Error(), string(errmsg))
			w.WriteHeader(400)
			w.Write(errmsg)
			return
		}

		response := string(output)

		if !strings.Contains(response, "node_info") {
			log.Printf("RESPONSE:  \n  %v", response)
		}

		w.Write([]byte(output))
	}
}

func getCmd(command string, escapeParams []string) *exec.Cmd {
	split := strings.Split(command, " ")

	escape := false
	tempStr := ""
	var cmdArray []string
	for i := 0; i < len(split); i ++ {
		s := split[i]

		if !escape {
			for _, e := range escapeParams {
				escape = strings.Index(s, e) == 0
				if escape {
					break
				}
			}
		}

		if escape {
			if tempStr == "" {
				println(tempStr)

				tempStr = s
				continue
			}

			// TODO 只根据"--"开头来判断是否已结束当前escape的参数，可能会导致bug
			// TODO Repair of bug, Conside Restructure
			if strings.Index(s, "--") == 0 {
				cmdArray = append(cmdArray, escapeQuotes(tempStr))
				escape = false
				tempStr = ""
				i--
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

	// fmt.Println(cmd.Args)

	return cmd
}

func escapeQuotes(tempStr string) string {
	tempStr = strings.Replace(tempStr, "\\\"", "$escape$", -1)
	tempStr = strings.Replace(tempStr, "\"", "", -1)
	tempStr = strings.Replace(tempStr, "$escape$", "\"", -1)
	return tempStr
}