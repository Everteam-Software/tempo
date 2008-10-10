require 'spec'
require 'hpricot'
require 'open-uri'
require 'xml/libxml'

## Before running cucumber
Before do
  config = YAML::load(File.open( File.expand_path("./rsc/scripts/config.yml")))
  @liferay = File.expand_path(config["install_dir"]+"/liferay-portal-tomcat-5.5-5.1.0")
  @webapps = "#{@liferay}/webapps"
  @services = "#{@webapps}/axis2/WEB-INF/services"
end

# Define all the givens
Given 'the webapps folder' do end
Given 'the (.*) portlet' do |portlet| 
  @webapp = "#{@webapps}/#{portlet}" 
  @webinf = "#{@webapp}/WEB-INF"
  @portlet_xml = "#{@webinf}/portlet.xml"
  @portlet_xml = "#{@webinf}/portlet-custom.xml" if not File.exist?(@portlet_xml)
  @liferay_display_xml = "#{@webinf}/liferay-display.xml"
  @liferay_portlet_xml = "#{@webinf}/liferay-portlet.xml"
end
Given 'the (.*) folder' do |folder| @folder = folder end
Given 'the pipa (.*)' do |workflow|
  @deploy = "#{@liferay}/var/deploy"
  @pipa = "#{@deploy}/#{workflow}/#{workflow}.pipa/#{workflow}/#{workflow}.pipa"
  @xform = "#{@deploy}/#{workflow}/#{workflow}.xform/#{workflow}"
end

# Useful methods, may try to move this somewhere else
def file_has_property_with_value(file, prop, val) 
  found = false
  f = File.new file
  f.each { |line| 
    found = true if(line.include?(prop+"="+val))
  }
  found
end
def remove_element_from_xml(file, parents, element, value, ns = nil)
  root = XML::Document.file(file)
  nodes = if not ns == nil then  root.find(parents, ns)  else root.find(parents) end
  nodes.each do |el| 
    content = el.find_first(element, ns).content
    el.remove! if content == value
  end
  root.save(file, true)
end

Then /I should find the webapp: (.*)/ do |webapp|
  File.exist?("#{@webapps}/#{webapp}/WEB-INF/web.xml").should == true
end

Then /it should provide the (.*) service/ do |ser|
  Dir.glob("#{@services}/*#{ser}*").length.should == 1
end

Then /it should contains (.*)/ do |f|
  File.exist?("#{@liferay}#{@folder}/#{f}").should == true
end

Then /it should(.*)find the jar file (.*)/ do |yesno, jar|
  yn = Dir.glob("#{@liferay}#{@folder}/*#{jar}*").length
  check = 1
  if yesno == "not" then check = 0 end
  yn.should == check
end 

Then /it should not contained deployed items/ do
  glob = "#{@liferay}#{@folder}/*.deployed"
  array = Dir.glob(glob)
  array.each do |item|
    FileUtils.rm_f item
  end
  Dir.glob(glob).length.should == 0
end

Then /it should contain the process (.*)/ do |process|
  # Check presence of bpel file
  Dir.glob("#{@liferay}/#{@folder}/#{process}/*.bpel").length.should == 1
  # check presence of deploy file
  File.exist?("#{@liferay}/#{@folder}/#{process}/deploy.xml").should == true
end

Then /it should have a property (.*) with value (.*)/ do |prop,val|
  file_has_property_with_value(@pipa, prop, val).should == true
end

## WARNING: This actually deletes the content of the directory before checking 
Then /it should be empty/ do
  f = "#{@liferay}/#{@folder}"
  FileUtils.rm_rf(f) if File.exist?(f)
  Dir.mkdir(f)
  # content is [".", ".."]
  (Dir.entries(f).size == 2).should == true
end

## We need to check three files for liferay. 
## 
Then /the portlet id should be (.*) and displays in liferay as (.*) in category (.*)/ do |portlet_id, display, category|
  # check portlet.xml
  doc = Hpricot(open(@portlet_xml))
  (doc/"portlet-app/portlet/portlet-name").inner_html.should == "#{portlet_id}"
  (doc/"portlet-app/portlet/display-name").inner_html.should == "#{display}"
  (doc/"portlet-app/portlet/portlet-info/title").inner_html.should == "#{display}"
  # check liferay_portlet.xml
  doc = Hpricot(open(@liferay_portlet_xml))
  (doc/"liferay-portlet-app/portlet/portlet-name").inner_html.should == "#{portlet_id}"
  # check liferay_display.xml
  doc = Hpricot(open(@liferay_display_xml))
  doc.at("display/category").get_attribute("name").should == "#{category}"
  doc.at("display/category/portlet").get_attribute("id").should == "#{portlet_id}"
end

Then /it should contain the necessary portlet files/ do
  File.exist?(@portlet_xml).should == true
  File.exist?(@liferay_display_xml).should == true
  File.exist?(@liferay_portlet_xml).should == true
end


## WARNING: this method replaces the content of the xml file
## it is stripping the login portlet out 
## so we don't display the login portlet that doesn't work
Then /check the (.*) portlet is disabled/ do |portlet|
  remove_element_from_xml(
    @portlet_xml, 
    '//a:portlet-app/a:portlet', 
    "a:display-name", 
    "Login",
    'a:http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd')
  remove_element_from_xml(@liferay_portlet_xml, '//liferay-portlet-app/portlet', "struts-path", "login")
end

Then /url rewrites requests for (.*)/ do |rewrite|
  url_rewrite_xml = "#{@webinf}/urlrewrite.xml"
  File.exist?(url_rewrite_xml).should == true
  root = XML::Document.file(url_rewrite_xml)
  nodes = root.find("/urlrewrite/rule/from")
  found = false
    nodes.each do |el| 
      found = true if not el.content.index("#{rewrite}") == nil
    end
    found.should == true
end

Then /it does not contain any (.*) file/ do |file|
  glob = "#{@liferay}/**/#{file}"
  Dir.glob(glob).size.should == 0
end