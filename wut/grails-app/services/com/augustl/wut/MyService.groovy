package com.augustl.wut

import grails.transaction.Transactional

@Transactional
class MyService {
    def grailsApplication

    @MyAstTransformingAnnotation
    void doSomething() {
        println "Hello from MyService#doSomething"
    }
}
