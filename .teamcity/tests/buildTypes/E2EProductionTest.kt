package tests.buildTypes

import common.E2ERunner
import common.extensions.isProjectPlayground
import common.stepE2ETest
import jetbrains.buildServer.configs.kotlin.buildFeatures.notifications
import jetbrains.buildServer.configs.kotlin.triggers.schedule


object E2EProductionTest : E2ERunner({
    name = "E2E Test in Production"

    params {
        param("env.BASE_URL", "https://kotlinlang.org")
    }

    triggers {
        schedule {
            enabled = !isProjectPlayground()
            schedulingPolicy = cron {
                seconds = "0"
                minutes = "0"
                hours = "6-18/2"
                dayOfMonth = "1/1"
                month = "*"
                dayOfWeek = "?"
            }
            triggerBuild = always()
            branchFilter = "+:master"
            withPendingChangesOnly = false
        }
    }

    steps {
        step(stepE2ETest())
    }

    features {
        notifications {
            branchFilter = "+:master"
            enabled = !isProjectPlayground()
            notifierSettings = slackNotifier {
                connection = "PROJECT_EXT_486"
                sendTo = "#kotlin-web-site-e2e-tests"
                messageFormat = simpleMessageFormat()
            }
            buildFailedToStart = true
            buildFailed = true
            buildProbablyHanging = true
        }
    }
})
