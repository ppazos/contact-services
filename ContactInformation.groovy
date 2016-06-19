package contact_services

// TODO: with a dictionary of names we can guess the names and put the rest on the lastname.

// EqualsAndHashCode allows to implement the minus as a -
// equals just by email excludes name and lastname fields http://docs.groovy-lang.org/latest/html/api/groovy/transform/EqualsAndHashCode.html
@groovy.transform.EqualsAndHashCode(excludes=["name","lastname"])
class ContactInformation {

   String email
   String name
   String lastname
   
   
   /**
    * contact_string has the form contact<line separator>contact<line separator>contact<line separator>...
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
   static List getContactInformationFromString(contact_string)
   {
      def res = []
      
      // split just by line separator, so is one contact per line!
      // denormalize transforms all end of lines to the platform end of line
      def contactList = contact_string.denormalize().split(System.getProperty("line.separator"))*.trim()
      
      // THE INPUT SHOULD REMOVE , because , can be also part of the name...
      // with tokenize allows to have newlines between contact information or commas
      //def contactList = contact_string.tokenize(",\n")*.trim()
      //contactList.removeAll([""]) // if a line has , and newline it generates an empty string elements on the list
      //println contactList
      
      def contactTokens
      contactList.each { contact ->
         contactTokens = contact.split(" ")
         
         // TODO: verify valid emails by regex: http://stackoverflow.com/questions/8204680/java-regex-email
         // TODO: report invalid emails
         // TODO: if name is email (case a@a.a <a@a.a>), avoid putting it in the name
         // TODO: if there are more than 5 parts, join all together in the name
         switch (contactTokens.size())
         {
            case 1:
              res << new ContactInformation(email: contactTokens[0].toLowerCase())
            break
            case 2:
              def name = contactTokens[0].replaceAll('"','')
              def email = contactTokens[1].toLowerCase().replaceAll('<','').replaceAll('>','')
              if (name == email) name = null // TODO: we can try to guess the name from the email user part like check if it is name.lastname
              res << new ContactInformation(name: name,
                                            email: email)
            break
            case 3:
              res << new ContactInformation(name: contactTokens[0].replaceAll('"',''),
                                            lastname: contactTokens[1].replaceAll('"',''), 
                                            email: contactTokens[2].toLowerCase().replaceAll('<','').replaceAll('>',''))
            break
            default:
               //println "++"+ contactTokens +" ++ "+ contactTokens[contactTokens.size()-1]
               
               def count = contactTokens.size()
               
               def email = contactTokens[count-1].toLowerCase().replaceAll('<','').replaceAll('>','') // TODO: check format!
               def name = "" // join the rest of the tokens in the name because we can't guess what is what
               for (int i in 0..count-2)
               {
                  name += contactTokens[i].replaceAll('"','') +" "
               }
               name = name[0..-2] // removes last space
               
               //println email
               
               res << new ContactInformation(name: name, email: email)
         }
      }
      
      return res
   }
   
   static boolean isValidEmail(String email)
   {
      java.util.regex.Pattern VALID_EMAIL_ADDRESS_REGEX = ~/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
      java.util.regex.Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email)
      return matcher.find()
   }
   
   /**
    * Gets a list of ContactInformation and returns a list without any duplicates. Uses the email to match contact info.
    */
   static List removeDuplicates(List contacts)
   {
      // TODO: keep the record that has more info
      contacts.unique { a, b -> a.email <=> b.email }
   }
   
   static List merge(List contacts1, List contacts2, boolean remove_duplicates)
   {
      def merge = contacts1 + contacts2
      
      if (remove_duplicates)
      {
         merge = removeDuplicates(merge)
      }
      
      return merge
   }
   
   static List minus(List from, List remove)
   {
      return from - remove
   }
   
   /**
    * Takes a list of ContactInformation, and returns a string with the CSV format, one field per column.
    * This is useful to import the contacts in systems like mailchimp.
    * cols: name and number of column [email: 1, name: 0, lastname: 2] if null by default is: [email: 0, name: 1, lastname: 2]
    */
   static String toCSV(Map cols, List contacts, boolean include_headers, String csv_separator = ',')
   {
      // TODO: check that cols is valid
      
      if (!cols) cols = [email: 0, name: 1, lastname: 2]
      
      // Sort entries by col #
      cols = cols.sort { it.value }
      
      String res = ""
      
      if (include_headers)
      {
         cols.each { name, index ->
            res += name + csv_separator
         }
         res = res[0..-2] // removes last ,
         res += System.getProperty("line.separator") // end of current row on csv
      }
      
      contacts.each { contact ->
         cols.each { name, index ->
            res += contact."$name" + csv_separator
         }
         res = res[0..-2] // removes last ,
         res += System.getProperty("line.separator") // end of current row on csv
      }
      
      return res
   }
   
   /**
    * cols: col names and indexes to know how to process the CSV, should be set by the user processing the CSV, they know the columns
    * csv: a csv formatted stirng
    * has_headers: to avoid processing the first row if it is the headers
    */
   static List fromCSV(Map cols, String csv, boolean has_headers, String csv_separator = ',')
   {
      // TODO: check that cols is valid
      csv = csv.denormalize() // csv with end of line of the platform
      
      def lines = csv.split(System.getProperty("line.separator"))
      def res = []
      def tokens
      def contact
      lines.each { line ->
      
         //println "l> "+ line +"<l"
      
         if (has_headers) // avoid first loop
         {
            has_headers = false
            return
         }
      
         tokens = line.split(csv_separator)*.trim() // gets columns and removes whitespaces from each column value
         
         //println "t> "+ tokens +"<t"
         
         contact = new ContactInformation()
         cols.each { name, index ->
            // names that have spaces in the CSV, have double quotes, the replaceAll removes those quotes
            contact."$name" = tokens[index].replaceAll('"','') // e.g. contact.email = value on colun $index
         }
         res << contact
      }
      
      return res
   }
   
   String toString()
   {
      this.email +" "+ this.name +" "+ this.lastname +System.getProperty("line.separator")
   }
}
