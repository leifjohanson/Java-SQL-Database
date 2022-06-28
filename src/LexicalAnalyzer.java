import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class LexicalAnalyzer {

    public static SurlyDatabase db = new SurlyDatabase();

  /* Parses the given file into individual commands
		and passes each to the appropriate parser */
    public void run(String fileName) throws FileNotFoundException{

      File file = new File(fileName);
      Scanner scan = new Scanner(file);

      LinkedList<Attribute> catalogSchema = new LinkedList<>();
      catalogSchema.add(new Attribute("RELATION", "CHAR", 13));
      catalogSchema.add(new Attribute("ATTRIBUTES", "NUM", 10));
      Relation catalog = new Relation("CATALOG", catalogSchema);
      db.createRelation(catalog);

      String currTotCmd = "";                                 // stores total command until semicolon is found
      String currTok;                                         // current token in command parse

      while (scan.hasNext())                                  // while more tokens are avalible
      {
        currTok = scan.next();                                // skip whitespace to first token

        if (currTok.charAt(0) == '#')                         // if first char of first token is comment syntax
        {
          scan.nextLine();                                    // then skip line
          continue;
        }
        
        currTotCmd = currTotCmd.concat(" ".concat(currTok));  // if not a command, begin adding tokens to currTotCmd
        if (currTok.contains(";"))                            // if semicolon is hit
        {
            exeCmd(currTotCmd);                               // test command and attempt to execute
            currTotCmd = "";                                  // reset currTotCmd for next command
        }
      }

      scan.close();
    }

    private static void exeCmd(String cmd)
    {
      Scanner scan = new Scanner(cmd);                        // setup scanner to tokenize command
      String cmdName = scan.next();                           // first token should be command name
      String cmdParams = cmd.substring(cmdName.length()+2, cmd.length()-1); // remove command name to get string only containing paramenters

      if (cmdName.equals("RELATION"))
      {
        Relation(cmdParams);
      }
      else if (cmdName.equals("INSERT"))
      {
        Insert(cmdParams, scan);
      }
      else if (cmdName.equals("PRINT"))
      {
        Print(cmdParams);
      }
      else if (cmdName.equals("DESTROY"))
      {
        Destroy(cmdParams, scan);
      }
      else if (cmdName.equals("DELETE"))
      {
        Delete(cmdParams, cmd, scan);
      }
      else 
      {
          String[] cmdParamsArr = cmdParams.split(" ");

          if (cmdParamsArr.length > 2 && cmdParamsArr[1].equals("SELECT"))
          {
            if (cmdParamsArr.length > 4 && cmdParamsArr[3].equals("WHERE"))
            {
              SelectWhereParser swp = new SelectWhereParser(Arrays.copyOfRange(cmdParamsArr, 2, cmdParamsArr.length), db, cmdName);
              swp.parseAddRelation();
            }
            else
            {
              SelectParser sp = new SelectParser(Arrays.copyOfRange(cmdParamsArr, 2, cmdParamsArr.length), db, cmdName);
              sp.parseAddRelation();
            }
          }
          else if (cmdParamsArr.length > 2 && cmdParamsArr[1].equals("PROJECT"))
          {
            ProjectParser pp = new ProjectParser(Arrays.copyOfRange(cmdParamsArr, 2, cmdParamsArr.length), cmdName, db);
            pp.projectRelation();
          }
          else if (cmdParamsArr.length > 2 && cmdParamsArr[1].equals("JOIN"))
          {
            JoinParser jp = new JoinParser(Arrays.copyOfRange(cmdParamsArr, 2, cmdParamsArr.length), cmdName, db);
            jp.joinRelations();
          }
      }
      // word is the variable name (assume good input) - MAX HANDLE ERROR HANDLING FOR BAD INPUT LATER PLEASE !!!

      scan.close();
    }

    public static void Relation(String cmdParams)
    {
        RelationParser rp = new RelationParser(cmdParams);
        Relation newRel = rp.parseRelation();
        db.createRelation(newRel);
        
        Relation catalog = db.getRelation("CATALOG");
        LinkedList<AttributeValue> preTuple = new LinkedList<>();
        preTuple.add(new AttributeValue());
        preTuple.getLast().setName("RELATION");
        preTuple.getLast().setValue(newRel.getName());
        preTuple.add(new AttributeValue());
        preTuple.getLast().setName("ATTRIBUTES");
        preTuple.getLast().setValue(Integer.toString(newRel.getSchema().size()));

        Tuple catalogTup = new Tuple(preTuple);
        catalog.insert(catalogTup);
    }

    public static void Insert(String cmdParams, Scanner scan)
    {
        InsertParser ip = new InsertParser(cmdParams);

        String relName = scan.next();           // relation name

        Relation rel = db.getRelation(relName);

        if (rel != null && rel.isTemp())
        {
          return;
        }

        Relation currRel = db.getRelation(relName);

        if (currRel != null)  // check that relation exists
        {
          Tuple newTup = ip.parseTuple();
          currRel.insert(newTup);
        }
        else
        {
          System.out.println("ERROR (INSERT): RELATION NOT FOUND");
        }
    }

    public static void Print(String cmdParams)
    {
        PrintParser pp = new PrintParser(cmdParams);
        String[] rels = pp.parseRelationNames();
        
        for (String relName : rels)
        {
          Relation rel = db.getRelation(relName);
          rel.print();
        }
    }

    public static void Destroy(String cmdParams, Scanner scan)
    {
        String relName = scan.next();

        if (relName.equals("CATALOG;"))
        {
          System.out.println("ERROR (DESTROY): CANNOT DESTROY RELATION (CATALOG)");
          scan.close();
          return;
        }

        Relation rel = db.getRelation(relName);

        if (rel != null && rel.isTemp())
        {
          return;
        }

        DestroyParser dp = new DestroyParser(cmdParams);
        Relation currRel = db.getRelation(dp.parseRelationName());

        if (currRel != null)
        {
          db.destroyRelation(currRel.getName());
          db.getRelation("CATALOG").removeElement(currRel.getName());
        }
        else
        {
          System.out.println("ERROR (DESTROY): RELATION NOT FOUND");
        }
    }

    public static void Delete(String cmdParams, String cmd, Scanner scan)
    {
        String relName = scan.next();

        if (relName.equals("CATALOG;"))
        {
          System.out.println("ERROR: CANNOT DELETE RELATION (CATALOG)");
          scan.close();
          return;
        }

        Relation rel = db.getRelation(relName);

        if (rel != null && rel.isTemp())
        {
          return;
        }

        String[] cmdWhereCheckArr = cmd.split(" ");
        cmdWhereCheckArr = Arrays.copyOfRange(cmdWhereCheckArr, 1, cmdWhereCheckArr.length);

        if(cmdWhereCheckArr[2].equals("WHERE")) 
        {
          DeleteWhereParser dwp = new DeleteWhereParser(cmdParams, db);
          dwp.parseDeleteTuples();
        } 
        else 
        {
          DeleteParser dp = new DeleteParser(cmdParams);
          Relation currRel = db.getRelation(dp.parseRelationName());

          if (currRel != null)
          {
            currRel.delete();
          }
          else
          {
            System.out.println("ERROR (DELETE): RELATION NOT FOUND");
          }
        }
    }
}
