package example

import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer

/**
  * @param image - current image, that we want to show
  * @param whatToShow - type of showing (fixation cross, question image, text question, rest)
  * @param isTesting - boolean flag representing test session or not
  * @param images - list of images
  * @param questionType - type of question
  *                     1 - is it a dog?
  *                     2 - is it animal?
  *                     3 - is it car?
  *                     4 - is it vehicle?
  *                     5 - is it nature?
  *                     6 - is it urban?
  *                     7 - is it indoor scene?
  *                     8 - is it positive interaction on scene?
  */
case class State(res: (UltraRapidImage, ArrayBuffer[UltraRapidImage]), whatToShow: WhatToShow, isTesting: Boolean,
                  questionType: Int, numberOfQuestions: Int)