package shared

/**
 *
 * @param imageType
 * 1 - dogs
 * 2 - animals
 * 3 - cars
 * 4 - vehicles
 * 5 - nature
 * 6 - urban
 * 7 - social
 * @param imageName - physical image name
 * @param preloaded - if image is in cache already
 *
 */
case class UltraRapidImage(imageType: String, imageName: String, var preloaded: Boolean) {
  def this(imageType: String, imageName: String) = this(imageType, imageName, false)

  override def toString: String = {
    s"$imageType,$imageName;"
  }


}
