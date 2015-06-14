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
 * @param imageName - physical image name
 *
 */
case class UltraRapidImage(imageType: String, imageName: String) {
  override def toString: String = {
    s"$imageType,$imageName;"
  }
}
