package edu.bastos.coaf.analyzer.domain.rule

import org.springframework.stereotype.Component

@Component
class HighValueRule : CoafRule {
    override val description = "Valor em espécie superior a R$ 50 mil"
    override val basisLegal = "Art. 10, Circular BACEN 3.978/2020"
    override val alert = "VALOR_ALTO_ESPECIE"

    override fun appliesTo(text: String): Boolean {
        val regex = Regex("""R?\$\s*(\d+[.\d]*)\s*(mil|mi)?.*(espécie|dinheiro)""", RegexOption.IGNORE_CASE)
        return regex.findAll(text).any { matchResult ->
            val value = matchResult.groupValues[1].replace(".", "").toDoubleOrNull() ?: 0.0
            val multiplication = when (matchResult.groupValues[2].lowercase()) {
                "mil" -> 1000.0
                "mi" -> 1_000_000.0
                else -> 1.0
            }
            value * multiplication > 50000
        }
    }
}