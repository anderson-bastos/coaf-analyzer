package edu.bastos.coaf.analyzer.domain.rule

import org.springframework.stereotype.Component

@Component
class StructuringRule : CoafRule {
    override val description = "Possível operação de structuring (fracionamento)"
    override val basisLegal = "Art. 2º, Lei 9.613/98"
    override val alert = "ESTRUTURAMENTO"

    override fun appliesTo(text: String): Boolean {
        val padroes = listOf(
            "fracionamento", "várias operações", "múltiplos depósitos",
            "valores abaixo", "evitar registro", "parcelado"
        )
        return padroes.any { it in text.lowercase() }
    }
}