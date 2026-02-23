package edu.bastos.coaf.analyzer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoafAnalyzerApplication

fun main(args: Array<String>) {
	runApplication<CoafAnalyzerApplication>(*args)
}
