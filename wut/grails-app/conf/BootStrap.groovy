import com.augustl.wut.MyService

class BootStrap {
    def myService

    def init = { servletContext ->
        myService.doSomething()

//        new MyService().doSomething()
    }
    def destroy = {
    }
}
