package edu.bastos.coaf.analyzer.domain.rule

import org.springframework.stereotype.Component

@Component
class ProfileIncompatibilityRule : CoafRule {
    override val description = "Operação incompatível com o perfil do cliente"
    override val basisLegal = "Art. 11, Circular BACEN 3.978/2020"
    override val alert = "INCOMPATIBILIDADE_PERFIL"

    override fun appliesTo(text: String): Boolean {
        val incompatibilidades = listOf(
            "professor.*\\d+.*mil",
            "comerciante.*\\d+.*milhões?",
            "renda.*\\d+.*incompatível",
            "primeira movimentação.*\\d+.*mil"
        )

        return incompatibilidades.any { pattern ->
            Regex(pattern, RegexOption.IGNORE_CASE).containsMatchIn(text)
        }
    }
}