package services.intro

case class IntroEvent(t: String, event: String, delay: Double, trigger: () => Unit)
