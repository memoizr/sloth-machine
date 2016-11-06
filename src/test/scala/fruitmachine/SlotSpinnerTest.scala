package fruitmachine

class SlotSpinnerTest extends BaseTest {

  it should "have 1 in 4/(4 pow 4) chances of being all equal" in {
    val slotSpinner = new SlotSpinner

    val samples = 10000
    val error = samples/100

    val size = (1 to samples).map { _ =>
      slotSpinner.spin
    }.count(_ == AllEqual)

    size.toDouble shouldBe ((4 / math.pow(4, 4)) * samples) +- error
  }
}
