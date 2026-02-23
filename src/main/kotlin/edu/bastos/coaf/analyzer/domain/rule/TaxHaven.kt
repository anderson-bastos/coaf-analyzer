package edu.bastos.coaf.analyzer.domain.rule

import org.springframework.stereotype.Component

@Component
class TaxHaven : CoafRule {
    override val description = "Envolvimento com paraíso fiscal"
    override val basisLegal = "Art. 11, Circular BACEN 3.978/2020"
    override val alert = "PARAISO_FISCAL"

    private val paraisosFiscais = listOf(
        "cayman", "panamá", "bahamas", "ilhas virgens", "suíça",
        "singapura", "dubai", "delaware", "luxemburgo", "chipre"
    )

    override fun appliesTo(text: String): Boolean {
        return paraisosFiscais.any { it in text.lowercase() }
    }
}