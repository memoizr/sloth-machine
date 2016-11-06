name := "fruit-machine"

version := "1.0"

scalaVersion := "2.11.8"

def scalatest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
def scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
def mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test"

libraryDependencies ++= Seq(
  scalatest,
  scalacheck,
  mockito
)
