//******************************************************************************
// Titre.java:	Applet
// Copyright (c) 1999 WBC
// www.alc.net/wbc
// wbc@alc.net
//
// L'auteur d�tient et gardes les copyright.
// Vous avez le droit de copier, modifier et compiler cet applet, mais vous devez
// demander l'autorisation � l'auteur pour le vendre ou vendre ses copies modifi�s
// ou non. Vous devez aussi laisser le nom, l'url du site et l'e-mail de l'auteur
// dans les sources et dans l'applet compil�.
//
// Desciption: Affiche un texte avec un d�grad� qui se d�place et de couleur variante.
//******************************************************************************
import java.applet.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

//==============================================================================
// Classe Main de l'applet Titre
//
//==============================================================================
public class Titre extends Applet implements Runnable
{
	private boolean ready = false;
	private int i = 0, j = 0;
	private int DegradPos=-50, r=255, v=0, b=0, iWidth, iHeight;
	private Font font;
	private Image offScrImage, imgDegradTxt;
	private Graphics offScrGC;
	private Color BgColor;

	// PRISE EN CHARGE PARAM�TRE:
	//		Les param�tres permettent � un auteur HTML de passer des informations � l'applet;
	// l'auteur HTML les indique � l'aide de la balise <PARAM> � l'int�rieur de la balise <APPLET>
	// Les variables suivantes servent � stocker les valeurs des
	// param�tres.
    //--------------------------------------------------------------------------

    // Membres des param�tres de l'applet
    // <type>       <VarMembre>    = <Valeur par d�faut>
    //--------------------------------------------------------------------------
	public String m_Texte = "www.alc.net/wbc";
	public String m_BgColor = "000000";
	public int m_FontSize = 40;
	public int m_FontStyle = Font.PLAIN;
	public int m_IncrCouleur = 10;
	public int m_DegradWidth = 200;
	public int m_BeginingDegradSize = 5;
	public int m_DegradSpeed = 20;

    // Noms des param�tres. Pour modifier le nom d'un param�tre,  il suffit d'apporter
	// une seule modification � la valeur de la cha�ne de param�tres ci-dessous.
    //--------------------------------------------------------------------------
	private final String PARAM_Texte = "Text";
	private final String PARAM_BgColor = "BgColor";
	private final String PARAM_FontSize = "FontSize";
	private final String PARAM_FontStyle = "FontStyle";
	private final String PARAM_IncrCouleur = "ColorChgSpeed";
	private final String PARAM_DegradWidth = "GradationSize";
	private final String PARAM_BeginingDegradSize = "BeginingDegradSize";
	private final String PARAM_DegradSpeed = "GradationMoveSpeed";

	// PRISE EN CHARGE THREAD:
	//		m_AEFF	est l'objet Thread de l'applet
	//--------------------------------------------------------------------------
	private Thread	 t = null;

	// Constructeur de classes Titre
	//--------------------------------------------------------------------------
	public Titre()
	{
		//  TODO: Ajoutez ici les lignes de code suppl�mentaires du constructeur
	}

	// PRISE EN CHARGE INFO APPLET:
	//		La m�thode getAppletInfo() retourne une cha�ne de caract�res indiquant l'auteur de
	// l'applet,  la date du copyright,  ou des informations diverses.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Nom : Titre\r\n" +
		       "Auteur: Werner BEROUX\r\n" +
			   "        http://www.alc.net/wbc\r\n" +
			   "        wbc@alc.net\r\n" +
		       "Cr�� avec Microsoft Visual J++ Version 1.1";
	}

	// PRISE EN CHARGE PARAM�TRE
	//		La m�thode getParameterInfo() retourne un tableau de cha�nes d�crivant
	// les param�tres reconnus par cette applet.
	//
    // Informations de param�tre AEFF:
    //  { "Nom", "Type", "Description" },
    //--------------------------------------------------------------------------
	public String[][] getParameterInfo()
	{
		String[][] info =
		{
			{ PARAM_Texte, "String", "Texte � afficher." },
			{ PARAM_BgColor, "String", "Couleur de fond en hexa." },
			{ PARAM_FontSize, "int", "Taille du texte." },
			{ PARAM_FontStyle, "int", "Normal=0, Gras=1, Italique=2" },
			{ PARAM_IncrCouleur, "int", "Vitesse � laquelle change la couleur du texte." },
			{ PARAM_DegradWidth, "int", "Longueur du d�grad� en pixels." },
			{ PARAM_BeginingDegradSize, "int", "Taille du Mini d�grad� au d�but du d�grad�." },
			{ PARAM_DegradSpeed, "int", "Pixels qu'avancera le d�grad� entre chaque affichage." }
		};
		return info;		
	}

	// La m�thode init() est appel�e par AWT lorsqu'une applet est charg�e pour la premi�re fois ou
	// recharg�e. Red�finissez cette m�thode pour effectuer l'initialisation n�cessaire � votre
	// applet,  telle que l'initialisation des structures de donn�es,   le chargement d'images ou
	// de polices,  la cr�ation de fen�tres ind�pendantes,  la configuration du gestionnaire de pr�sentation,  ou l'ajout de
	// composants de l'interface utilisateur.
    //--------------------------------------------------------------------------
	public void init()
	{
		//		Le code suivant permet de r�cup�rer la valeur de chaque param�tre
		// indiqu�e par la balise <PARAM> et de la stocker dans une variable
		// de membre.
		//----------------------------------------------------------------------
		String param;

		// Texte: Texte � afficher.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_Texte);
		if (param != null)
			m_Texte = param;

		// BgColor: Couleur de fond en hexa.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_BgColor);
		if (param != null)
			BgColor = new Color(Integer.parseInt(param, 16));

		// FontSize: Taille du texte.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_FontSize);
		if (param != null)
			m_FontSize = Integer.parseInt(param);

		// FontStyle: Gras, Italique, etc.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_FontStyle);
		if (param != null)
			m_FontStyle = Integer.parseInt(param);

		// IncrCouleur: Vitesse � laquelle change la couleur du texte.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_IncrCouleur);
		if (param != null)
			m_IncrCouleur = Integer.parseInt(param);
		
		// DegradWidth: Longueur du d�grad� en pixels.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_DegradWidth);
		if (param != null)
			m_DegradWidth = Integer.parseInt(param);

		// BeginingDegradSize: Taille du Mini d�grad� au d�but du d�grad�.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_BeginingDegradSize);
		if (param != null)
			m_BeginingDegradSize = Integer.parseInt(param);

		// DegradSpeed: Pixels qu'avancera le d�grad� entre chaque affichage.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_DegradSpeed);
		if (param != null)
			m_DegradSpeed = Integer.parseInt(param);


		// Taille de l'applet
		iWidth = size().width;
		iHeight = size().height;

		// Fonte
		font = new Font("Courier", m_FontStyle, m_FontSize);

		// Cr�� un Graphic off
		offScrImage = createImage(iWidth, iHeight);
		offScrGC = offScrImage.getGraphics();

		// Change la couleur du fond
		setBackground(BgColor);
	}

	// Ins�rez ici des lignes de code suppl�mentaires de l'applet destin�es � quitter proprement le syst�me. La m�thode destroy() est appel�e
	// lorsque votre applet se termine et est d�charg�e.
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// TODO: Ins�rez ici le code de l'applet destin� � quitter proprement le syst�me
	}

	public void repaint()
	{
		update(getGraphics());
		paint(getGraphics());
	}

	public void update(Graphics g)
	{
		// Efface l'�cran off
		if (ready)
		{
			offScrGC.setColor(BgColor);
			offScrGC.fillRect(0,0, iWidth,iHeight);
		}
	}

	// Gestionnaire de dessin Titre
	//--------------------------------------------------------------------------
	public void paint(Graphics g)
	{
		if (ready)
		{
			ready = false;
			if (offScrGC.drawImage(imgDegradTxt, DegradPos,0, this))		// Affiche le texte d�grad� sur l'�cran off
				g.drawImage(offScrImage, 0,0, this);						// Affiche � l'�cran
			// Efface la m�moire
			if (imgDegradTxt != null)
				imgDegradTxt.flush();
		}
	}

	//		La m�thode start() est appel�e lorsque la page contenant l'applet
	// s'affiche en premier � l'�cran. L'impl�mentation initiale de l'Assistant Applet
	// pour cette m�thode d�marre l'ex�cution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void start()
	{
		if (t == null)
		{
			t = new Thread(this);
			t.start();
		}
	}
	
	//		La m�thode stop() est appel�e lorsque la page contenant l'applet
	// dispara�t de l'�cran. L'impl�mentation initiale de l'Assistant Applet
	// pour cette m�thode arr�te l'ex�cution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void stop()
	{
		if (t != null)
		{
			t.stop();
			t = null;
		}
	}

	// PRISE EN CHARGE THREAD
	//		La m�thode run() est appel�e lorsque la thread de l'applet est d�marr�e.
	// Si votre applet effectue des activit�s permanentes sans attendre la saisie de donn�es par l'utilisateur
	// le code impl�mentant ce comportement s'ins�re en r�gle g�n�rale ici. Par
	// exemple,  pour une applet qui r�alise une animation,  la m�thode run() g�re
	// l'affichage des images.
	//--------------------------------------------------------------------------
	public void run() 
	{
		while (t != null)
			calc();
	}

	private void calc()
	{
		int pixResule[] = new int[m_DegradWidth * iHeight];

		// Si le texte change, affiche le nouveau texte.
		String sLastTxtToDraw = m_Texte;

		int BkR = BgColor.getRed(),
			BkG = BgColor.getGreen(),
			BkB = BgColor.getBlue();
		int textWidth = m_Texte.length() * m_FontSize * 5/8;
		int startDegradPos = (iWidth - textWidth)/2 - m_DegradWidth - 10;
		int endDegradPos   = (iWidth + textWidth)/2 + 10;
		int Alpha;

		// Affiche le texte
		Image offScrTxtImg = createImage(iWidth, iHeight);
		Graphics offScrTxt = offScrTxtImg.getGraphics();
		offScrTxt.setColor(Color.black);
		offScrTxt.fillRect(0,0, iWidth,iHeight);
		offScrTxt.setColor(Color.white);
		offScrTxt.setFont(font);
		offScrTxt.drawString(m_Texte,
							 (iWidth - textWidth)/2,
							 m_FontSize / 2 + 10);

		// Pixels du texte
		int pixTexte[] = new int[iWidth * iHeight];
		PixelGrabber pixelgrabber = new PixelGrabber(offScrTxtImg, 0,0, iWidth,iHeight, pixTexte, 0, iWidth);
		try
		{
			pixelgrabber.grabPixels();
		}
		catch(InterruptedException ie)
		{
			System.out.println("Erreur: Impossible de capturer les pixels.");
			return;
		}

		while (true)
		{
			try
			{
				// Y'a quelqueschose � aff. ?
				if (m_Texte.length() == 0 || m_Texte != sLastTxtToDraw)
				{
					// Commence au d�but.
					DegradPos = -150;
					// Efface la m�moire
					if (offScrTxtImg != null)
						offScrTxtImg.flush();
					break;
				}

				// Change la couleur
				if (r >= 255)
					if (b > 0)
						b -= m_IncrCouleur;
					else
						v += m_IncrCouleur;

				if (v >= 255)
					if (r > 0)
						r -= m_IncrCouleur;
					else
						b += m_IncrCouleur;

				if (b >= 255)
					if (v > 0)
						v -= m_IncrCouleur;
					else
						r += m_IncrCouleur;

				if (r>255) r=255;
				if (r<0) r=0;
				if (v>255) v=255;
				if (v<0) v=0;
				if (b>255) b=255;
				if (b<0) b=0;

				
				// Cr�� le grand d�grad� de fin & l'applique au texte
				for (i=0; i<m_DegradWidth-m_BeginingDegradSize && iWidth-DegradPos > i; i++)
				{
					if (DegradPos+i >= 0)											// Ne calcule pas ce qui est avant l'�cran
					{
/*						degradR = i * (r-BgColor.getRed()  ) / (m_DegradWidth-m_BeginingDegradSize-1) + BgColor.getRed();
						degradG = i * (v-BgColor.getGreen()) / (m_DegradWidth-m_BeginingDegradSize-1) + BgColor.getGreen();
						degradB = i * (b-BgColor.getBlue() ) / (m_DegradWidth-m_BeginingDegradSize-1) + BgColor.getBlue();
						for (j=0; j<iHeight; j++)
						{
							Alpha = (pixTexte[j*iWidth+i+DegradPos] & 255);
							pixResule[j*m_DegradWidth+i] = (255 << 24) |				// Couche alpha
														   ((Alpha & degradR) << 16) |	// Rouge
														   ((Alpha & degradG) << 8 ) |	// Vert
														   (Alpha & degradB);			// Blue
						}*/
						for (j=0; j<iHeight; j++)
							pixResule[j*m_DegradWidth+i] =	((pixTexte[j*iWidth+i+DegradPos] & 255) << 24) |				// Couche alpha
															(i * (r-BkR ) / (m_DegradWidth-m_BeginingDegradSize-1) + BkR) << 16|	// Rouge
															(i * (v-BkG ) / (m_DegradWidth-m_BeginingDegradSize-1) + BkG) << 8 |	// Vert
															(i * (b-BkB ) / (m_DegradWidth-m_BeginingDegradSize-1) + BkB);			// Bleu
					}
				}
				// Cr�� mini d�grad� de d�but & l'applique au texte
				for (i=m_DegradWidth-m_BeginingDegradSize; i<m_DegradWidth && iWidth-DegradPos > i; i++)
				{
/*					if (DegradPos+i >= 0)											// Ne calcule pas ce qui est avant l'�cran
					{
						degradR = (m_DegradWidth-i) * (r-BgColor.getRed()  ) / m_BeginingDegradSize + BgColor.getRed();
						degradG = (m_DegradWidth-i) * (v-BgColor.getGreen()) / m_BeginingDegradSize + BgColor.getGreen();
						degradB = (m_DegradWidth-i) * (b-BgColor.getBlue() ) / m_BeginingDegradSize + BgColor.getBlue();
						for (j=0; j<iHeight; j++)
							pixResule[j*m_DegradWidth+i] = ((pixTexte[j*iWidth+i+DegradPos] & 255) << 24) |	// Couche alpha
														    (degradR << 16) |		// Rouge
														    (degradG << 8 ) |		// Vert
														    degradB;				// Blue
					}*/
					if (DegradPos+i >= 0)											// Ne calcule pas ce qui est avant l'�cran
					{
						for (j=0; j<iHeight; j++)
							pixResule[j*m_DegradWidth+i] =	((pixTexte[j*iWidth+i+DegradPos] & 255) << 24) |	// Couche alpha
															((m_DegradWidth-i) * (r-BkR) / m_BeginingDegradSize + BkR) << 16|	// Rouge
															((m_DegradWidth-i) * (v-BkG) / m_BeginingDegradSize + BkG) << 8 |	// Vert
															((m_DegradWidth-i) * (b-BkB) / m_BeginingDegradSize + BkB);			// Bleu
					}
				}
				// Cr�� l'image*
				if (imgDegradTxt != null)
					imgDegradTxt.flush();
				imgDegradTxt = createImage(new MemoryImageSource(m_DegradWidth, iHeight, pixResule, 0, m_DegradWidth));
				ready = true;

				// demande � afficher
				repaint();
				Thread.sleep(50);

				// d�place le scroll
				if (DegradPos > endDegradPos)
					DegradPos = startDegradPos;
				else
					DegradPos += m_DegradSpeed;
			}
			catch (InterruptedException ie)
			{
				// TODO: Ins�rez ici les lignes de code destin�es � traiter les exceptions au cas o�
				//       InterruptedException lev�e par Thread.sleep(),
				//		 ce qui signifie qu'une autre thread a interrompu celle-ci
				stop();
			}
		}
	}
}
