// IUserService.aidl
package dev.rikka.shizuku;

interface IUserService {
    void destroy();
    void exit();
    boolean executeCommand(String command);
    String doSomething();
}