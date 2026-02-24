package tests.buildTypes

import common.E2ERunner
import common.stepE2ETest
import jetbrains.buildServer.configs.kotlin.FailureAction
import kotlinlang.builds.BuildSitePages

object E2ETests : E2ERunner({
    name = "E2E Branch tests"

    requirements {
        exists("docker.server.version")
    }

    dependencies {
        dependency(BuildSitePages) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
                onDependencyCancel = FailureAction.CANCEL
            }
            artifacts {
                buildRule = sameChainOrLastFinished()
                artifactRules = "+:pages.zip!** => dist/"
            }
        }
    }

    steps {
        step(stepE2ETest("./scripts/test/run-e2e-tests.sh"))
    }
})
