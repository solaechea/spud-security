package spud.admin
import  spud.cms.*
import  spud.core.*

@SpudApp(name="Pages", thumbnail="spud/admin/pages_thumb.png")
@SpudSecure(['PAGES'])
class PagesController {
	static namespace = 'spud_admin'
	def grailsApplication
  def spudTemplateService

  def index = {
  	def pages = SpudPage.list([sort: 'pageOrder', spudPage: null] + params)
		render view: '/spud/admin/pages/index', model:[pages: pages, pageCount: SpudPage.count()]
  }

  def create = {
  	def page = new SpudPage()
    def templateService = spudTemplateService.activeTemplateService()
  	def layoutsForSite  = templateService.layoutsForSite(0)
  	def defaultLayoutName = grailsApplication.config.spud.cms.defaultLayout ?: 'application'
  	def defaultLayout = layoutsForSite.find { it.name == defaultLayoutName }
  	def partials = []
  	if(defaultLayout) {
  		defaultLayout.partials.each {
  			partials << new SpudPagePartial(name: it.key, content: null)
  		}
  	}
  	render view: '/spud/admin/pages/create', model:[page: page, layouts: layoutsForSite, partials: partials]
  }

  def save = {
    if(!params.page) {
      flash.error = "Page submission not specified"
      redirect controller: 'pages', action: 'index', namespace: 'spud_admin'
      return
    }

    def page = new SpudPage(params.page)

    params.partial.each { partial ->
      def partialRecord = new SpudPagePartial(name: partial.key, content: partial.value)
      page.addToPartials(partialRecord)
    }

    if(page.save(flush:true)) {
      redirect controller: 'pages', action: 'index', namespace: 'spud_admin'
    } else {
      flash.error = "Error Saving Page"

      def templateService   = spudTemplateService.activeTemplateService()
      def layoutsForSite    = templateService.layoutsForSite(0)
      def defaultLayoutName = page.layout ?: grailsApplication.config.spud.cms.defaultLayout ?: 'application'
      def defaultLayout     = layoutsForSite.find { it.name == defaultLayoutName }
      def partials = []
      if(defaultLayout) {
        defaultLayout.partials.each {
          partials << new SpudPagePartial(name: it.key, content: null)
        }
      }
      render view: '/spud/admin/pages/create', model:[page: page, layouts: layoutsForSite, partials: partials]
    }

  }

  def edit = {
  	def page = loadPage()
  }

  def update = {
  	def page = loadPage()
  }

  def delete = {
  	def page = loadPage()
  }


  private loadPage() {
  	if(!params.id) {
			flash.error = "Page Submission not specified"
			redirect controller: 'pages', action: 'index', namespace: 'spud_admin'
			return null
		}

		def page = SpudPage.get(params.id)
		if(!page) {
			flash.error = "Page not found!"
			redirect controller: 'pages', action: 'index', namespace: 'spud_admin'
			return null
		}
		return page
  }


}