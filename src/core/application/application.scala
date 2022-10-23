package core.application

import core.kernel.*
import core.event.*

class Application() extends core.kernel.process.Process {
    val chrono = core.kernel.Chronometer(120.0)
    override val id: String = "Application"
    // val gator = new Artifact("boat", "id")
    override def update(): Unit = {

    }
}


