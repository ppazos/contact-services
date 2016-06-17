package contact_services

class ContactInformation {

   String email
   String name
   String lastname
   
   
   /**
    * contactString has the form contact, contact, contact, ...
    * where contact can be:
    * - email
    * - name <email>
    * - name lastname <email>
    * - name1 name2 lastname <email>
    * - name lastname1 lastname2 <email>
    * - name1 name2 lastname1 lastname2 <email>
    * - "name" <email>
    * - "name lastname" <email>
    * - "name1 name2 lastname" <email>
    * - "name lastname1 lastname2" <email>
    * - "name1 name2 lastname1 lastname2" <email>
    *
    * and between contact there can be any number of whitespaces or new lines
    */
   static List getContactInformationFromString(contactString)
   {
      def res = []
      def contactList = contactString.split(",")*.trim()
      
      def contactTokens
      contactList.each { contact ->
         contactTokens = contact.split(" ")
         
         // TODO: verify valid emails by regex: http://stackoverflow.com/questions/8204680/java-regex-email
         // TODO: if name is email (case a@a.a <a@a.a>), avoid putting it in the name
         // TODO: if there are more than 5 parts, join all together in the name
         switch (contactTokens.size())
         {
            case 1:
              res << new ContactInformation(email: contactTokens[0])
            break
            case 2:
              res << new ContactInformation(name: contactTokens[0].replaceAll('"',''), email: contactTokens[1].replaceAll('<','').replaceAll('>',''))
            break
            case 3:
              res << new ContactInformation(name: contactTokens[0].replaceAll('"',''),
                                            lastname: contactTokens[1].replaceAll('"',''), 
                                            email: contactTokens[2].replaceAll('<','').replaceAll('>',''))
            break
            case 4:
            break
            case 5:
            break
            default:
               throw Exception("contact has "+ contactTokens.size() + " parts")
         }
      }
      
      return res
   }
   
   /**
    * Gets a list of ContactInformation and returns a list without any duplicates. Uses the email to match contact info.
    */
   static List removeDuplicates(List contacts)
   {
      contacts.unique { a, b -> a.email <=> b.email }
   }
   
   /**
    * Takes a list of ContactInformation, and returns a string with the CSV format, one field per column.
    * This is useful to import the contacts in systems like mailchimp.
    */
   static String toCSV(List contacts)
   {
   }
   
   String toString()
   {
      this.email +" "+ this.name +" "+ this.lastname
   }
}