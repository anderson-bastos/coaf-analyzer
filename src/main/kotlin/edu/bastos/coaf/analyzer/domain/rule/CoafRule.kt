package edu.bastos.coaf.analyzer.domain.rule

interface CoafRule {
    val description: String
    val basisLegal: String
    val alert: String
    fun appliesTo(text: String): Boolean
}