package modules.jsonorg

object Module extends modules.Module {
  def toSeq(array:org.json.JSONArray):Seq[AnyRef] = {
    Range(0, array.length).map(array.get(_))
  }
}
