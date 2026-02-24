package common

import jetbrains.buildServer.configs.kotlin.BuildStep
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep

open class E2ERunner(init: BuildType.() -> Unit) : BuildType({
    artifactRules = """
        +:test-results/* => test-results.zip
    """.trimIndent()

    params {
        param("env.WEBTEAM_UI_NPM_TOKEN", "%WEBTEAM_UI_NPM_TOKEN%")
        param("env.CI", "true")
    }

    vcs {
        root(vcsRoots.KotlinLangOrg)
    }

    init()
})

// language=sh
val DEFAULT_STEP_E2E_TEST = """
    yarn install --immutable
    yarn test:ci
""".trimIndent()

fun stepE2ETest(content: String = DEFAULT_STEP_E2E_TEST): BuildStep {
    return ScriptBuildStep {
        name = "Run E2E tests"
        scriptContent = content
        dockerImage = "mcr.microsoft.com/playwright:v1.57.0"
        dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
    }
}
