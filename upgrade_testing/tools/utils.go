package tools

import (
	"fmt"
	"time"
	"os"
	"strings"
	"path/filepath"
	"errors"
	"io"
	"bufio"
	"os/exec"
	"io/ioutil"
)

func ExecCommand(commandName string, Params []string, isPrint bool, isPassword bool) string {
	cmd := exec.Command(commandName, Params...)
	fmt.Println(cmd.Args)

	stdout, _ := cmd.StdoutPipe()
	pipeIn, _ := cmd.StdinPipe()

	cmd.Start()

	if isPassword {
		fmt.Println("enter password : "+PASSWORD)
		time.Sleep(time.Duration(DURATION)*time.Second)
		pipeIn.Write([]byte(PASSWORD+ "\n"))
	}

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

	return str
}

func Debug(msg string){
	fmt.Println(msg)
	time.Sleep(time.Duration(DURATION)*time.Second)
}

func Rm(params []string)error{
	for _, file := range params {
		f := ROOT+ file
		err := os.RemoveAll(f)
		if err != nil {
			return err
		}
	}

	fmt.Println("Files remove OK!")
	return nil
}

func GetBetweenStr(str, start, end string) string {
	n := strings.Index(str, start)
	if n == -1 {
		n = 0
	}
	str = string([]byte(str)[n:])
	m := strings.Index(str, end)
	if m == -1 {
		m = len(str)
	}
	str = string([]byte(str)[:m])
	return str
}

func ReadGentxFile(name string) (string, error) {
	files, _ := filepath.Glob(ROOT + name + "/config/gentx/*")
	if len(files) == 0{
		return "", errors.New("Read node error")
	}

	var str string
	if str,Err = read(files[0]); Err != nil {
		return "", Err
	}

	return str,nil
}

func CopyFile(srcName, destName string)(int64, error){
	srcFile ,Err:= os.Open(srcName)
	if Err!=nil{
		return 0, Err
	}
	defer srcFile.Close()
	destFile,Err:=os.OpenFile(destName,os.O_WRONLY|os.O_CREATE,0777)
	if Err!=nil{
		return 0, Err
	}
	defer destFile.Close()
	return io.Copy(destFile,srcFile)
}

func CopyDir(src string, dest string)error{
	src_original := src
	src_file:= ""
	Err := filepath.Walk(src, func(src string, f os.FileInfo, Err error) error {
		if f == nil {
			return Err
		}

		if src == src_original {
			return nil
		}

		dest_new := strings.Replace(src, src_original, dest, -1)

		src_file = src

		_ , Err = CopyFile(src, dest_new)
		if Err != nil{
			return Err
		}

		return nil
	})

	if Err != nil {
		return errors.New("CopyDir error"+Err.Error())
	}

	fmt.Println("CopyFile "+src_file+" ok !")
	return nil
}

func write(name,content string) error {
	data := []byte(content)
	if ioutil.WriteFile(name,data,0644) != nil {
		return errors.New(name+ "write file error")
	}
	fmt.Println(name + " write file ok !")
	return nil
}

func read(path string) (string,error) {
	fi, Err := os.Open(path)
	if Err != nil {
		return "",Err
	}
	defer fi.Close()

	fd, Err := ioutil.ReadAll(fi)
	return string(fd),nil
}


