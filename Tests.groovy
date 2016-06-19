package contact_services

def path = "C:/Documents and Settings/pab/My Documents/GitHub/contact-services/"

File sourceFile = new File(path + "ContactInformation.groovy")
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
GroovyObject contactInfo = (GroovyObject) groovyClass.newInstance();

/*
// TEST 1

def test1 = """abreu.gerardo@gmail.com <Abreu.gerardo@gmail.com>
adson.franca@saude.gov.br <adson.franca@saude.gov.br>
Alvarez Buron Oscar <oalvarezb@saludcastillayleon.es>
AMSJ - Miguel P. Diaz Hernandez <mpdh@amsj.com.uy>
Andrés F. Zapata Osorio <azapata@linkdx.com.co>
beatriz@hc.ufmg.br <beatriz@hc.ufmg.br>
cbrown@genexusconsulting.com <cbrown@genexusconsulting.com>
Chamorro Gordejuela Jacobo <jchamorrog@saludcastillayleon.es>
Cisneros Martin M Angeles <mcisnerosm@saludcastillayleon.es>
clarecicardoso@yahoo.com.br <clarecicardoso@yahoo.com.br>
cleinaldocosta@uol.com.br <cleinaldocosta@uol.com.br>
D' Agostino Mr. Marcelo (WDC) <dagostim@paho.org>
"""

// tiene 7 partes genera problemas "AMSJ - Miguel P. Lopez Perez" <asdf@rrrrr.com.uy>,

def contactsss = contactInfo.getContactInformationFromString(test1)
println contactsss
*/



// TEST 2

File csv1_file = new File(path + "VER QUE NO ESTEN YA EN EX-ALUMN.csv")
def contacts = contactInfo.getContactInformationFromString(csv1_file.text)
println contacts.size() + " contacts 1"

File csv2_file = new File(path + "Contactos para difusión de cursos - Sheet1.csv")
def contacts2 = contactInfo.getContactInformationFromString(csv2_file.text)
println contacts2.size() + " contacts 2"

def contacts3 = contactInfo.merge(contacts, contacts2, true)

println contacts3.size() + " contacts 3"

def csv = contactInfo.toCSV([email: 0, name: 1, lastname: 2], contacts3, true, ',') // in spanish the csv sep is : in english it's ,

def out = new File(path + "out.csv")
out.withWriter('UTF-8') { it << csv }

println ""
println ""



// TEST 3

// read the output csv to test loading
def in_csv = out.text
def imported_contacts = contactInfo.fromCSV([email: 0, name: 1, lastname: 2], in_csv, true, ',') // in spanish the csv sep is : in english it's ,
//println imported_contacts[0..30]



// TEST 4 load mailchimp csv
def in_csv2 = new File(path + "mailchimp_openehr_ex_alumnos.csv").text
def imported_mailchimp = contactInfo.fromCSV([email: 0, name: 1, lastname: 2], in_csv2, true, ',') // in spanish the csv sep is : in english it's ,
//println imported_mailchimp[0..30]

def in_csv3 = new File(path + "mailchimp_isis_cbdc_ex_alumnos.csv").text
def imported_mailchimp2 = contactInfo.fromCSV([email: 0, name: 1, lastname: 2], in_csv3, true, ',') // in spanish the csv sep is : in english it's ,


// TEST 5 my contacts minus the mailchimp contacts
def sent_to = contactInfo.merge(imported_mailchimp2, imported_mailchimp, true) // merges 2 lists that I already sent emails to, with duplicates removed
def yet_not_sent1 = contactInfo.minus(imported_contacts, sent_to) // my new list minus the emails I already sent
def yet_not_sent1_csv = contactInfo.toCSV([email: 0, name: 1, lastname: 2], yet_not_sent1, true, ',') // in spanish the csv sep is : in english it's ,
def yet_not_sent1_out = new File(path + "yet_not_sent1.csv")

yet_not_sent1_out.withWriter('UTF-8') { it << yet_not_sent1_csv }

println ""
println ""