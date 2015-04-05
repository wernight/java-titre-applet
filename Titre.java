//******************************************************************************
// Titre.java:	Applet
// Copyright (c) 1999 WBC
// www.alc.net/wbc
// wbc@alc.net
//
// L'auteur détient et gardes les copyright.
// Vous avez le droit de copier, modifier et compiler cet applet, mais vous devez
// demander l'autorisation à l'auteur pour le vendre ou vendre ses copies modifiés
// ou non. Vous devez aussi laisser le nom, l'url du site et l'e-mail de l'auteur
// dans les sources et dans l'applet compilé.
//
// Desciption: Affiche un texte avec un dégradé qui se déplace et de couleur variante.
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

	// PRISE EN CHARGE PARAMÈTRE:
	//		Les paramètres permettent à un auteur HTML de passer des informations à l'applet;
	// l'auteur HTML les indique à l'aide de la balise <PARAM> à l'intérieur de la balise <APPLET>
	// Les variables suivantes servent à stocker les valeurs des
	// paramètres.
    //--------------------------------------------------------------------------

    // Membres des paramètres de l'applet
    // <type>       <VarMembre>    = <Valeur par défaut>
    //--------------------------------------------------------------------------
	public String m_Texte = "www.alc.net/wbc";
	public String m_BgColor = "000000";
	public int m_FontSize = 40;
	public int m_FontStyle = Font.PLAIN;
	public int m_IncrCouleur = 10;
	public int m_DegradWidth = 200;
	public int m_BeginingDegradSize = 5;
	public int m_DegradSpeed = 20;

    // Noms des paramètres. Pour modifier le nom d'un paramètre,  il suffit d'apporter
	// une seule modification à la valeur de la chaîne de paramètres ci-dessous.
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
		//  TODO: Ajoutez ici les lignes de code supplémentaires du constructeur
	}

	// PRISE EN CHARGE INFO APPLET:
	//		La méthode getAppletInfo() retourne une chaîne de caractères indiquant l'auteur de
	// l'applet,  la date du copyright,  ou des informations diverses.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Nom : Titre\r\n" +
		       "Auteur: Werner BEROUX\r\n" +
			   "        http://www.alc.net/wbc\r\n" +
			   "        wbc@alc.net\r\n" +
		       "Créé avec Microsoft Visual J++ Version 1.1";
	}

	// PRISE EN CHARGE PARAMÈTRE
	//		La méthode getParameterInfo() retourne un tableau de chaînes décrivant
	// les paramètres reconnus par cette applet.
	//
    // Informations de paramètre AEFF:
    //  { "Nom", "Type", "Description" },
    //--------------------------------------------------------------------------
	public String[][] getParameterInfo()
	{
		String[][] info =
		{
			{ PARAM_Texte, "String", "Texte à afficher." },
			{ PARAM_BgColor, "String", "Couleur de fond en hexa." },
			{ PARAM_FontSize, "int", "Taille du texte." },
			{ PARAM_FontStyle, "int", "Normal=0, Gras=1, Italique=2" },
			{ PARAM_IncrCouleur, "int", "Vitesse à laquelle change la couleur du texte." },
			{ PARAM_DegradWidth, "int", "Longueur du dégradé en pixels." },
			{ PARAM_BeginingDegradSize, "int", "Taille du Mini dégradé au début du dégradé." },
			{ PARAM_DegradSpeed, "int", "Pixels qu'avancera le dégradé entre chaque affichage." }
		};
		return info;		
	}

	// La méthode init() est appelée par AWT lorsqu'une applet est chargée pour la première fois ou
	// rechargée. Redéfinissez cette méthode pour effectuer l'initialisation nécessaire à votre
	// applet,  telle que l'initialisation des structures de données,   le chargement d'images ou
	// de polices,  la création de fenêtres indépendantes,  la configuration du gestionnaire de présentation,  ou l'ajout de
	// composants de l'interface utilisateur.
    //--------------------------------------------------------------------------
	public void init()
	{
		//		Le code suivant permet de récupérer la valeur de chaque paramètre
		// indiquée par la balise <PARAM> et de la stocker dans une variable
		// de membre.
		//----------------------------------------------------------------------
		String param;

		// Texte: Texte à afficher.
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

		// IncrCouleur: Vitesse à laquelle change la couleur du texte.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_IncrCouleur);
		if (param != null)
			m_IncrCouleur = Integer.parseInt(param);
		
		// DegradWidth: Longueur du dégradé en pixels.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_DegradWidth);
		if (param != null)
			m_DegradWidth = Integer.parseInt(param);

		// BeginingDegradSize: Taille du Mini dégradé au début du dégradé.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_BeginingDegradSize);
		if (param != null)
			m_BeginingDegradSize = Integer.parseInt(param);

		// DegradSpeed: Pixels qu'avancera le dégradé entre chaque affichage.
		//----------------------------------------------------------------------
		param = getParameter(PARAM_DegradSpeed);
		if (param != null)
			m_DegradSpeed = Integer.parseInt(param);


		// Taille de l'applet
		iWidth = size().width;
		iHeight = size().height;

		// Fonte
		font = new Font("Courier", m_FontStyle, m_FontSize);

		// Créé un Graphic off
		offScrImage = createImage(iWidth, iHeight);
		offScrGC = offScrImage.getGraphics();

		// Change la couleur du fond
		setBackground(BgColor);
	}

	// Insérez ici des lignes de code supplémentaires de l'applet destinées à quitter proprement le système. La méthode destroy() est appelée
	// lorsque votre applet se termine et est déchargée.
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// TODO: Insérez ici le code de l'applet destiné à quitter proprement le système
	}

	public void repaint()
	{
		update(getGraphics());
		paint(getGraphics());
	}

	public void update(Graphics g)
	{
		// Efface l'écran off
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
			if (offScrGC.drawImage(imgDegradTxt, DegradPos,0, this))		// Affiche le texte dégradé sur l'écran off
				g.drawImage(offScrImage, 0,0, this);						// Affiche à l'écran
			// Efface la mémoire
			if (imgDegradTxt != null)
				imgDegradTxt.flush();
		}
	}

	//		La méthode start() est appelée lorsque la page contenant l'applet
	// s'affiche en premier à l'écran. L'implémentation initiale de l'Assistant Applet
	// pour cette méthode démarre l'exécution de la thread de l'applet.
	//--------------------------------------------------------------------------
	public void start()
	{
		if (t == null)
		{
			t = new Thread(this);
			t.start();
		}
	}
	
	//		La méthode stop() est appelée lorsque la page contenant l'applet
	// disparaît de l'écran. L'implémentation initiale de l'Assistant Applet
	// pour cette méthode arrête l'exécution de la thread de l'applet.
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
	//		La méthode run() est appelée lorsque la thread de l'applet est démarrée.
	// Si votre applet effectue des activités permanentes sans attendre la saisie de données par l'utilisateur
	// le code implémentant ce comportement s'insère en règle générale ici. Par
	// exemple,  pour une applet qui réalise une animation,  la méthode run() gère
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
				// Y'a quelqueschose à aff. ?
				if (m_Texte.length() == 0 || m_Texte != sLastTxtToDraw)
				{
					// Commence au début.
					DegradPos = -150;
					// Efface la mémoire
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

				
				// Créé le grand dégradé de fin & l'applique au texte
				for (i=0; i<m_DegradWidth-m_BeginingDegradSize && iWidth-DegradPos > i; i++)
				{
					if (DegradPos+i >= 0)											// Ne calcule pas ce qui est avant l'écran
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
				// Créé mini dégradé de début & l'applique au texte
				for (i=m_DegradWidth-m_BeginingDegradSize; i<m_DegradWidth && iWidth-DegradPos > i; i++)
				{
/*					if (DegradPos+i >= 0)											// Ne calcule pas ce qui est avant l'écran
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
					if (DegradPos+i >= 0)											// Ne calcule pas ce qui est avant l'écran
					{
						for (j=0; j<iHeight; j++)
							pixResule[j*m_DegradWidth+i] =	((pixTexte[j*iWidth+i+DegradPos] & 255) << 24) |	// Couche alpha
															((m_DegradWidth-i) * (r-BkR) / m_BeginingDegradSize + BkR) << 16|	// Rouge
															((m_DegradWidth-i) * (v-BkG) / m_BeginingDegradSize + BkG) << 8 |	// Vert
															((m_DegradWidth-i) * (b-BkB) / m_BeginingDegradSize + BkB);			// Bleu
					}
				}
				// Créé l'image*
				if (imgDegradTxt != null)
					imgDegradTxt.flush();
				imgDegradTxt = createImage(new MemoryImageSource(m_DegradWidth, iHeight, pixResule, 0, m_DegradWidth));
				ready = true;

				// demande à afficher
				repaint();
				Thread.sleep(50);

				// déplace le scroll
				if (DegradPos > endDegradPos)
					DegradPos = startDegradPos;
				else
					DegradPos += m_DegradSpeed;
			}
			catch (InterruptedException ie)
			{
				// TODO: Insérez ici les lignes de code destinées à traiter les exceptions au cas où
				//       InterruptedException levée par Thread.sleep(),
				//		 ce qui signifie qu'une autre thread a interrompu celle-ci
				stop();
			}
		}
	}
}
