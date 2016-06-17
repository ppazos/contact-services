package contact_services

File sourceFile = new File("C:/Documents and Settings/pab/My Documents/GitHub/contact-services/ContactInformation.groovy")
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
GroovyObject myObject = (GroovyObject) groovyClass.newInstance();

def test1 = $/
"abreu.a@gmail.com" <Abreu.a@gmail.com>,
"adson.d@saude.gov.br" <adson.d@saude.gov.br>,
"Alvarez Perez, Oscar" <oalvarezb@abcd.es>,

"Andrés F. Sopoao Caral" <fdfd@fff.com.co>,
asdfffdadfasdf@gmail.com,
Carlos López Flores <car@fdsf.cl>,
Roberto Saez <fdsfsdfsdf@gmail.com>,
Roberto <hjkh@gmail.com>,
/$

// tiene 7 partes genera problemas "AMSJ - Miguel P. Lopez Perez" <asdf@rrrrr.com.uy>,


myObject.getContactInformationFromString(test1)