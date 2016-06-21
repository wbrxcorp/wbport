package profiles.migration

object ImportModule extends profiles.ImportModule {
  def modules:Seq[String] = Seq("common","wbport","wbport.migration")
}
