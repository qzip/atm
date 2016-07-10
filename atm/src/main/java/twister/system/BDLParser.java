/**
*
* @author Ashish Banerjee , 24-jan-2001
* (c) Osprey Software Technology P. Ltd., 2001, All rights reserved.
*  tested with jdk 1.3, will it work with jdk 1.2 ?
 * AB: 8-apr-11 : fixed Logger
 * AB: 03-APR-15 : Properties patch line 258
*/
/*
* 
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both

* (a) the Software, and

* (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a “Larger Work” to which the Software is contributed by such licensors),
* 
* without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms.
* 
* This license is subject to the following condition:
* 
* The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* 
*/

package twister.system;

import java.util.Hashtable;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
REM given below is an example for BASIC DEPLOYMENT LANGUAGE
REM this is a comment line
OBJECT logx As com.ospreyindia.DefaLogger
OBJECT  pop As Popper
WITH pop
  SET host = "localhost"
  SET port = 110
END WITH
RUN pop
**/
public class BDLParser   {
    public BDLParser() {
      this(new Hashtable());
    }
    public BDLParser(Hashtable tab) {
       this.sysTab = tab;
       this.log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);;
       init();
    }
    public void setLogger(Logger ps) {
       log = ps;
    }
    public synchronized void exec(InputStream in) throws IOException, IllegalArgumentException {
           rdr = new BufferedReader(new InputStreamReader(in));
           int errCnt = 0;
           lineCnt=0;
           while(loop && (errCnt < 10)) {
               lineCnt++;
               cmdLn = rdr.readLine();
               if(cmdLn != null) {
                  try {
                      cmdLn = cmdLn.trim();
                      if(cmdLn.startsWith(";"))
                          continue; // while
			if(cmdLn.length() > 0)
                          exec(cmdLn);
                     errCnt = 0; // reset err
                  }
                  catch(Exception ex) {
                     log(ex);
                     log("WARN: PROC=BDLParser; ID=102;  MSG=cmd unrecognized ["+cmdLn+"];");
                     errCnt++;
                  }

               }
               else
                 loop = false;
           } // while

    }
    protected void exec(String ln) throws Exception {
        tox = new StreamTokenizer(new StringReader(ln));
        tox.resetSyntax();   // reset is needed as StreamTokenizer constructor calls parseNumbers()
        tox.wordChars('a', 'z');
        tox.wordChars('A', 'Z');
        tox.wordChars(128 + 32, 255);
        tox.whitespaceChars(0, ' ');
        tox.commentChar('/');
        tox.quoteChar('"');
        tox.quoteChar('\'');
        tox.wordChars('0','9');
        tox.wordChars('.','.');
        tox.wordChars('$','$');

         if(tox.nextToken() != tox.TT_WORD)
            throw new IllegalArgumentException("Expected command at line["+ lineCnt+"]");
         String x = tox.sval.toUpperCase();
         verbose("INFO: About to exec ["+x+"]",9);

         Command cmd = (Command)cmdTab.get(x);
         if(cmd != null)
           cmd.exec(tox,sysTab);
         else
           log("ERROR: PROC=BDLParser; ID=112; MSG=Ignoring Unknown Commnd ["+x+"]");

    }
    protected void log(String msg) {
       log.warning(msg);
    }
    protected void log(Exception ez) {
        log.severe(ez.toString());
        ez.printStackTrace();
    }
    protected void verbose(String msg, int lev) {
        Level curLev = log.getLevel();
        int tlev = (curLev == null)? 1: curLev.intValue();
        if( lev <= tlev)
           log.log(curLev, msg);
    }
    private Hashtable sysTab = null;
    protected BufferedReader rdr=null;
    protected boolean loop = true;
    protected StreamTokenizer tox = null;
    protected Hashtable cmdTab = null;
    protected static Object curWith = null;
    protected String cmdLn = null;
    protected int lineCnt = 0;
    protected Logger log ; 
    protected BeanSetter  beanMan = new BeanSetter();

    protected void init() {
       cmdTab = new Hashtable();
       cmdTab.put("REM",new CmdRem());
       cmdTab.put("RUN",new CmdRun());
       cmdTab.put("WITH",new CmdWith());
       cmdTab.put("END",new CmdEnd());
       CmdObject obx = new CmdObject();
       cmdTab.put("OBJECT",obx);
       cmdTab.put("DIM",obx);
       cmdTab.put("SET",new CmdSet());
       cmdTab.put("EXIT",new CmdExit());
    }
    public void registerCommand(String cmd, BDLParser.Command target) {
       if(cmd != null && target != null) {
           cmdTab.put(cmd.toUpperCase(), target);
       }
    }

    public static void main(String args[]) throws Exception {
        if(args.length != 1) {
           System.out.println("usage BDLParser <BDL file name>");
           System.exit(1);
        }
        System.out.println("UPAY 09-June-2018: www.BonBiz.in ");
/**
        System.out.println("Twister SmsPush/Pull Technology Version 1.2 Release 20010127");
        if(System.currentTimeMillis() > 997879027150L) {
		   System.out.println("Trial Period expired\n call info@ospreyindia.com");
           System.exit(1);
        }
        else
           System.out.println("Trial Period till 15-Aug-2k1");
*/
        BDLParser cmds = new BDLParser();
        cmds.exec(new FileInputStream(args[0]));
        // DO NOT DO: System.exit(1); if you have servers threads running
    }

    public interface Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception;
    }
    //----
    class CmdRun implements Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {
           if(tok.nextToken() != tok.TT_WORD) {
              log("ERROR: PROC=BDLParser.CmdRun; ID=104;  MSG=Format expected RUN <Runnable Obj ref>");
           }
           else {
               String x = tok.sval;
               Object rx = ctx.get(x);
               if(rx == null)
                  log("ERROR: PROC=BDLParser.CmdRun; LINE="+lineCnt+"; ID=105;  MSG= Ref. ["+x+"] not found in sysTab");
               else if(rx instanceof Thread) {
                  verbose("INFO: PROC=BDLParser.CmdRun; LINE="+lineCnt+"; ID=106;  MSG=Executing thread ["+x+"];",2);
                  ((Thread)rx).start();
               }
               else if(rx instanceof Runnable ) {
                  verbose("INFO: PROC=BDLParser.CmdRun; LINE="+lineCnt+"; ID=107;  MSG=Executing Runnable ["+x+"];",2);
                  (new Thread((Runnable)rx)).start();
               }
               else {
                  log("ERROR: PROC=BDLParser.CmdRun; LINE="+lineCnt+"; ID=105;  MSG= Ref. ["+x+"] is not Thread or Runnable");
               }

           }

       }
    } // CmdRun
    class CmdRem implements Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {

       }
    }
    class CmdExit implements Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {
            log("PROC=BDLParser.CmdExit; LINE="+lineCnt+"; ID=999;  MSG=INFO: executing Exit command");
            System.exit(1);
       }
    }
    class CmdObject implements Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {
          // OBJECT <name> {As|Is|=} <Class Name>
          //       ^
          if(tok.nextToken() != tok.TT_WORD)
             log("ERROR: PROC=BDLParser.CmdRun; LINE="+lineCnt+"; ID=105;  MSG=An Object reference name expected;");
          else {
            String ref = tok.sval;
            tok.nextToken(); // skip "As"
            if(tok.nextToken() == tok.TT_WORD) {
                String clz = tok.sval;
                ctx.put(ref,Class.forName(clz).newInstance());
            }
            else
             log("ERROR: PROC=BDLParser.CmdRun; LINE="+lineCnt+"; ID=105;  MSG=An Object Class Name expected for["+ref+"];");
          }
       }
    }
    class CmdSet implements Command {
       private String lhs = null, rhs = null;
       private Object ref = null;
        @Override
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {
           lhs = rhs = null; ref = null;
           // SET  <property> = <value or ref>
           //     ^
          if(curWith == null)
             log("ERROR: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=302;  MSG=Use WITH command before SET;");
          else {
           // SET  <property> = <value or ref>
           //                ^
            if(tok.nextToken() == tok.TT_WORD) {
                lhs = tok.sval;
               // SET  <property> = <value or ref>
               //                  ^
               verbose("SET: lhs="+lhs,9);
               if(tok.nextToken() == tok.TT_EOF)
                  log("ERROR: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=105;  MSG=Expecting '=' ;");
               // SET  <property> = <value or ref>
               //                                 ^
               else if(tok.nextToken() != tok.TT_EOF) {
                 rhs = tok.sval;
                 verbose("SET: rhs="+rhs, 9);
                 // if rhs is of word type then check context for ref else
                 //     assume rhs to be litteral
                 if(tok.ttype == tok.TT_WORD) {
                    ref = ctx.get(rhs);
                    if(ref == null) {
                      verbose("INFO: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=106;  MSG=Assuming literal <rval> as ["+ref+"] not found in Object Context Table;",3);
                    }
                 }
                 processRval();
               }
               else
                 log("ERROR: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=107;  MSG=Parsed till {SET <property> =} Was expecting <rval> ;");
            }
            else
             log("ERROR: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=108;  MSG=Invalid syntax for SET <property > = <rval>;");
          }
       } // cmd exec

       private void processRval() throws Exception {
          // This method is called only if parsing is successfull
          // 1st try to call curWith.set<lval>(<ref. of rval>) method
           // AB: 03-APR-15 : Properties patch
           

          if(curWith instanceof java.util.Properties) {
              Properties p = (Properties)curWith;
              p.setProperty(lhs, rhs);
          }
          String setPat =  beanMan.changeToSetPattern(lhs);
          Object param = (ref == null)? rhs : ref;
          if(!beanMan.assignObject(curWith,setPat, param)) {
             // failed: 2nd try call curWith.setProperty(<lval>,<rval>)
             verbose("SET: ref = " + ref,9);

			 if((ref == null) && beanMan.assignString(curWith,setPat,rhs)) {
			     verbose("INFO: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=105;  MSG=Assigned value using ["+setPat+"] method;",3);
             }
             else if((ref == null) && beanMan.assignProperty(curWith,lhs,rhs))
                 verbose("INFO: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=105;  MSG=Assigned value using ["+setPat+"] method;",3);
             else
               log("ERROR: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=106;  MSG=;");
          }
          else
            verbose("INFO: PROC=BDLParser.CmdSet; LINE="+lineCnt+"; ID=107;  MSG=Assigned value using ["+setPat+"] method;",3);



       } // processRval()

    }
    class CmdWith implements Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {
          if(curWith != null)
             log("WARN: PROC=BDLParser.CmdWith; LINE="+lineCnt+"; ID=300;  MSG=IGNORING WITH, Use END command explictly;");
          if(tok.nextToken() == tok.TT_EOF) {
             log("ERROR: PROC=BDLParser.CmdWith; LINE="+lineCnt+"; ID=310;  MSG=SYNTAX ERROR, WITH <object ref.>");
          }
          else {
			  String temp = tok.sval;
             curWith = ctx.get(temp);
             System.out.println(temp);
             if(curWith == null)
               log("ERROR: PROC=BDLParser.CmdWith; LINE="+lineCnt+"; ID=310;  MSG=INVALID Object ref. ["+tok.sval+"];");
          }
       }
    }
    class CmdEnd implements Command {
       public void exec(StreamTokenizer tok, Hashtable ctx) throws Exception {
          if(curWith == null)
             log("WARN: PROC=BDLParser.CmdEnd; LINE="+lineCnt+"; ID=301;  MSG=IGNORING END, Use WITH before END command;");
          curWith = null;
       }
    }
    //----
}

