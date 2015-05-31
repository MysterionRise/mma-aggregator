package example

import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element
import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._

import scala.scalajs.js
import scala.scalajs.js.Function0

object UltraRapidTest {

  var testingStarted = false
  var canvas: Canvas = _
  var ctx: CanvasRenderingContext2D = _

  def clearText(element: Element): Function0[Any] = new Function0[Any] {
    override def apply(): Any = {
      element.textContent = "TAKE A REST"
      element.textContent = "REST REST"
    }
  }

  def askQuestion(div: Div): Function0[Any] = new Function0[Any] {

    override def apply(): Any = {
      testingStarted = true
      div.innerHTML = ""
      val element = getElementById[Element]("ultra-test")
      element.textContent = "Did you see car here?"
    }
  }

  def showImage(div: Div, imageName: String): Function0[Any] = new Function0[Any] {
    override def apply(): Any = {
      div.innerHTML = "<img src=\"/assets/images/ultraRapid/" + imageName + "\">"
    }
  }


  /**
   *
   * @param imageName - image name, could be empty
   * @param whatToShow 1 - fixation cross, 2 - image, 3 - question, 4 - rest time
   *                   subject for refactoring
   */
  case class State(imageName: String, whatToShow: Int)

  class Backend($: BackendScope[_, State]) {
    var interval: js.UndefOr[js.timers.SetIntervalHandle] =
      js.undefined

    def showPicture() =
      $.modState(s => {
        val sp = s.imageName.split("\\.")
        val num = Integer.parseInt(sp(0))
        State((num + 1).toString + "." + sp(1), s.interval, s.whatToShow + 1)
        // todo check if more pictures available
      })

    def init() =
    // todo create new report
      interval = js.timers.setInterval($.state.interval)(showPicture())
  }

  //  val testApp = ReactComponentB[Unit]("TestApp")
  //    .initialState(State("551.jpg", 1333, 1))
  //    .backend(new Backend(_))
  //    .render((P, S, B) =>
  //    img(src := "/assets/images/ultraRapid/" + S.imageName))
  //    .componentDidMount(_.backend.init()) // executed during first render
  //    .componentWillUnmount(_.backend.interval foreach js.timers.clearInterval) // executed before clean up
  //    .buildU


  val testApp = ReactComponentB[Unit]("TestApp")
    .initialState(State("", 0))
    .backend(new Backend(_))
    .render((P, S, B) => div(
    h1("This is a test!")
  ))
    .componentDidMount(f => {

  })

    //    scope.mo
    //    // make ajax call here to get pics from instagram
    //    import scalajs.js.Dynamic.{global => g}
    //    val url = "https://api.instagram.com/v1/media/popular?client_id=642176ece1e7445e99244cec26f4de1f&callback=?"
    //    g.jsonp(url, (result: js.Dynamic) => {
    //      if (result != js.undefined && result.data != js.undefined) {
    //        val data = result.data.asInstanceOf[js.Array[js.Dynamic]]
    //        val pics = data.toList.map(item => Picture(item.id.toString, item.link.toString, item.images.low_resolution.url.toString, if (item.caption != null) item.caption.text.toString else ""))
    //        scope.modState(_ => State(pics, Nil))
    //      }
    //    })
    //  })
    .buildU

  def doTest() = {
    val question = getElementById[Div]("ultra-rapid")
    React.render(testApp(), question)
    //    //    val btn = getElementById[Button]("rapid-button")
    //    //    btn.onclick = {
    //    //      (e: dom.MouseEvent) =>
    //    for (i <- 0 until 5) {
    //      val question = getElementById[Div]("ultra-rapid")
    //
    //      canvas = getElementById[Canvas]("ultra-canvas")
    //      ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    //      val element = getElementById[Element]("ultra-test")
    //
    //      dom.window.setTimeout(drawFixationCross, 1)
    //      dom.window.setTimeout(showImage(question, "518.jpg"), 501)
    //      dom.window.setTimeout(askQuestion(question), 534)
    //      dom.window.setTimeout(clearText(element), 1534)
    ////      dom.window.setTimeout(drawFixationCross, 3534)
    //    }
    //
    //    dom.document.onkeypress = {
    //      (e: dom.KeyboardEvent) =>
    //        if (testingStarted && e.charCode == 32) {
    //          getElementById[Element]("ultra-test").textContent = "SPACE PRESSED!"
    //
    //        }
    //    }
  }

  def drawFixationCross(): Function0[Any] = new Function0[Any] {
    override def apply(): Any = {
      ctx.fillStyle = "gray"
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx.beginPath()
      ctx.strokeStyle = "white"
      val centerX = canvas.width / 2
      val centerY = canvas.height / 2
      ctx.moveTo(centerX, centerY)
      ctx.lineTo(centerX + 50, centerY + 50)
      ctx.stroke()
      ctx.moveTo(centerX, centerY)
      ctx.lineTo(centerX - 50, centerY + 50)

      ctx.stroke()
      ctx.moveTo(centerX, centerY)
      ctx.lineTo(centerX + 50, centerY - 50)

      ctx.stroke()
      ctx.moveTo(centerX, centerY)
      ctx.lineTo(centerX - 50, centerY - 50)

      ctx.stroke()
      ctx.closePath()
    }
  }

  private def clearDiv(div: Div): Function0[Any] = {
    new Function0[Any] {
      override def apply(): Any = div.innerHTML = "<canvas id=\"ultra-canvas\"/>"
    }
  }
}
