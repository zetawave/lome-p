package me.lo.lomefree.Globals;

import android.os.Environment;
import android.widget.Toast;
import java.io.File;

import static me.lo.lomefree.Interfaces.HiderPaths.HIDER_PATH_DIR;

/**
 * Created by Fabrizio Amico on 23/03/18.
 */

public class GlobalValues
{
    // Valori standard
    public static final String DBOX_APP_KEY = "removed";
    public static final String DBOX_APP_SECRET = "removed";
    public static final String CLOUD_RAIL_APP_KEY = "removed";
    public static final String downloadPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    public static final String rootDirectory = String.valueOf(Environment.getRootDirectory().getAbsolutePath());
    public static final String sdCardPath = String.valueOf(Environment.getExternalStorageDirectory());
    public static final String hiderPath = sdCardPath.concat(File.separator.concat(HIDER_PATH_DIR));
    public static final String defaultLomePathName = "Lome Archivio";
    public static final String defaultLomePath = sdCardPath + File.separator + defaultLomePathName;
    public static final String GalleryLomePath = getGalleryPath().concat("Lome");

    public static final String help_section_site = "https://lo2meinfo.wixsite.com/lome/help";
    public static final String terms_section_site = "https://lo2meinfo.wixsite.com/lome/termini-condizioni";
    public static final String privacy_section_site = "https://lo2meinfo.wixsite.com/lome/norme-sulla-privacy";
    public static final String website = "https://lo2meinfo.wixsite.com/lome";
    //ADS
    public static final String adsInterstitialHiderBannerID = "removed";
    public static final String adsInterstitialProgressActBannerID = "removed";
    public static final String adsInterstitialKeyMakerChoice = "removed";


    public static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator;
    }
    public static final int N_KEY_MODALITY = 3;
    public static final int SPLASH_SLEEP_TIME = 1000;

    //TOAST Mode
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int LONG = Toast.LENGTH_LONG;
    public static final int SHORT = Toast.LENGTH_SHORT;


    public static final String [][] LICENCES = new String[][]{{"zip4j", "Copyright 2010 Srikanth Reddy Lingala\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License."}, {"barcodescanner:zxing", "Copyright (c) 2014 Dushyanth Maguluru\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License."}, {"commons-io", "Apache Commons IO\n" +
            "Copyright 2002-2017 The Apache Software Foundation\n" +
            "\n" +
            "This product includes software developed at\n" +
            "The Apache Software Foundation (http://www.apache.org/)."}, {"Android-SpinKit", "The MIT License (MIT)\n" +
            "\n" +
            "Copyright © 2016 ybq\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."},
            {"CircleImageView", "Copyright 2014 - 2018 Henning Dodenhof\n" +
                    "\n" +
                    "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    "you may not use this file except in compliance with the License.\n" +
                    "You may obtain a copy of the License at\n" +
                    "\n" +
                    "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                    "\n" +
                    "Unless required by applicable law or agreed to in writing, software\n" +
                    "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    "See the License for the specific language governing permissions and\n" +
                    "limitations under the License."},
            {"Fancy Buttons", "Copyright 2014 El Mehdi Sakout\n" +
                    "\n" +
                    "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
                    "\n" +
                    "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
                    "\n" +
                    "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."},
            {"Apache Commons codec", "Copyright 2002-2017 The Apache Software Foundation\n" +
                    "\n" +
                    "This product includes software developed at\n" +
                    "The Apache Software Foundation (http://www.apache.org/)."}, {"appintro", " AppIntro library\n" +
            "\n" +
            "   Copyright 2015 Paolo Rotolo\n" +
            "   Copyright 2018 APL Devs\n" +
            "\n" +
            "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "   you may not use this file except in compliance with the License.\n" +
            "   You may obtain a copy of the License at\n" +
            "\n" +
            "       http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "   Unless required by applicable law or agreed to in writing, software\n" +
            "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "   See the License for the specific language governing permissions and\n" +
            "limitations under the License."}, {"Material Drawer", " Copyright 2016 Mike Penz\n" +
            "\n" +
            "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "   you may not use this file except in compliance with the License.\n" +
            "   You may obtain a copy of the License at\n" +
            "\n" +
            "       http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "   Unless required by applicable law or agreed to in writing, software\n" +
            "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "   See the License for the specific language governing permissions and\n" +
            "limitations under the License."},
            {"Passcode View", "Copyright 2017 Keval Patel\n" +
                    "\n" +
                    "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at\n" +
                    "\n" +
                    "http://www.apache.org/licenses/LICENSE-2.0\n" +
                    "\n" +
                    "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."},
            {"ToggleButton Group", "Copyright 2018 nex3z\n" +
                    "\n" +
                    "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    "you may not use this file except in compliance with the License.\n" +
                    "You may obtain a copy of the License at\n" +
                    "\n" +
                    "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                    "\n" +
                    "Unless required by applicable law or agreed to in writing, software\n" +
                    "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    "See the License for the specific language governing permissions and\n" +
                    "limitations under the License."},{"Expandable TextView", "Copyright 2014 Manabu Shimobe\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License."},{"Material About Library", "Copyright 2016-2018 Daniel Stone\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License."},{"Browser my Files", "\n" +
            "MIT License\n" +
            "\n" +
            "Copyright (c) 2017 adityak368\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
            "of this software and associated documentation files (the \"Software\"), to deal\n" +
            "in the Software without restriction, including without limitation the rights\n" +
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
            "copies of the Software, and to permit persons to whom the Software is\n" +
            "furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE."}, {"Multiple Media Picker", "Copyright 2016 Erika Gutierrez\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License."}, {"Tap Target View", "Copyright 2016 Keepsafe Software Inc.\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License."}, {"Encryptor4j", "The MIT License (MIT)\n" +
            "\n" +
            "Copyright (c) 2016 Martin Withaar\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
            "of this software and associated documentation files (the \"Software\"), to deal\n" +
            "in the Software without restriction, including without limitation the rights\n" +
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
            "copies of the Software, and to permit persons to whom the Software is\n" +
            "furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE."}, {"NoNonsenseApps filepicker", "Mozilla Public License Version 2.0\n" +
            "==================================\n" +
            "\n" +
            "1. Definitions\n" +
            "--------------\n" +
            "\n" +
            "1.1. \"Contributor\"\n" +
            "    means each individual or legal entity that creates, contributes to\n" +
            "    the creation of, or owns Covered Software.\n" +
            "\n" +
            "1.2. \"Contributor Version\"\n" +
            "    means the combination of the Contributions of others (if any) used\n" +
            "    by a Contributor and that particular Contributor's Contribution.\n" +
            "\n" +
            "1.3. \"Contribution\"\n" +
            "    means Covered Software of a particular Contributor.\n" +
            "\n" +
            "1.4. \"Covered Software\"\n" +
            "    means Source Code Form to which the initial Contributor has attached\n" +
            "    the notice in Exhibit A, the Executable Form of such Source Code\n" +
            "    Form, and Modifications of such Source Code Form, in each case\n" +
            "    including portions thereof.\n" +
            "\n" +
            "1.5. \"Incompatible With Secondary Licenses\"\n" +
            "    means\n" +
            "\n" +
            "    (a) that the initial Contributor has attached the notice described\n" +
            "        in Exhibit B to the Covered Software; or\n" +
            "\n" +
            "    (b) that the Covered Software was made available under the terms of\n" +
            "        version 1.1 or earlier of the License, but not also under the\n" +
            "        terms of a Secondary License.\n" +
            "\n" +
            "1.6. \"Executable Form\"\n" +
            "    means any form of the work other than Source Code Form.\n" +
            "\n" +
            "1.7. \"Larger Work\"\n" +
            "    means a work that combines Covered Software with other material, in\n" +
            "    a separate file or files, that is not Covered Software.\n" +
            "\n" +
            "1.8. \"License\"\n" +
            "    means this document.\n" +
            "\n" +
            "1.9. \"Licensable\"\n" +
            "    means having the right to grant, to the maximum extent possible,\n" +
            "    whether at the time of the initial grant or subsequently, any and\n" +
            "    all of the rights conveyed by this License.\n" +
            "\n" +
            "1.10. \"Modifications\"\n" +
            "    means any of the following:\n" +
            "\n" +
            "    (a) any file in Source Code Form that results from an addition to,\n" +
            "        deletion from, or modification of the contents of Covered\n" +
            "        Software; or\n" +
            "\n" +
            "    (b) any new file in Source Code Form that contains any Covered\n" +
            "        Software.\n" +
            "\n" +
            "1.11. \"Patent Claims\" of a Contributor\n" +
            "    means any patent claim(s), including without limitation, method,\n" +
            "    process, and apparatus claims, in any patent Licensable by such\n" +
            "    Contributor that would be infringed, but for the grant of the\n" +
            "    License, by the making, using, selling, offering for sale, having\n" +
            "    made, import, or transfer of either its Contributions or its\n" +
            "    Contributor Version.\n" +
            "\n" +
            "1.12. \"Secondary License\"\n" +
            "    means either the GNU General Public License, Version 2.0, the GNU\n" +
            "    Lesser General Public License, Version 2.1, the GNU Affero General\n" +
            "    Public License, Version 3.0, or any later versions of those\n" +
            "    licenses.\n" +
            "\n" +
            "1.13. \"Source Code Form\"\n" +
            "    means the form of the work preferred for making modifications.\n" +
            "\n" +
            "1.14. \"You\" (or \"Your\")\n" +
            "    means an individual or a legal entity exercising rights under this\n" +
            "    License. For legal entities, \"You\" includes any entity that\n" +
            "    controls, is controlled by, or is under common control with You. For\n" +
            "    purposes of this definition, \"control\" means (a) the power, direct\n" +
            "    or indirect, to cause the direction or management of such entity,\n" +
            "    whether by contract or otherwise, or (b) ownership of more than\n" +
            "    fifty percent (50%) of the outstanding shares or beneficial\n" +
            "    ownership of such entity.\n" +
            "\n" +
            "2. License Grants and Conditions\n" +
            "--------------------------------\n" +
            "\n" +
            "2.1. Grants\n" +
            "\n" +
            "Each Contributor hereby grants You a world-wide, royalty-free,\n" +
            "non-exclusive license:\n" +
            "\n" +
            "(a) under intellectual property rights (other than patent or trademark)\n" +
            "    Licensable by such Contributor to use, reproduce, make available,\n" +
            "    modify, display, perform, distribute, and otherwise exploit its\n" +
            "    Contributions, either on an unmodified basis, with Modifications, or\n" +
            "    as part of a Larger Work; and\n" +
            "\n" +
            "(b) under Patent Claims of such Contributor to make, use, sell, offer\n" +
            "    for sale, have made, import, and otherwise transfer either its\n" +
            "    Contributions or its Contributor Version.\n" +
            "\n" +
            "2.2. Effective Date\n" +
            "\n" +
            "The licenses granted in Section 2.1 with respect to any Contribution\n" +
            "become effective for each Contribution on the date the Contributor first\n" +
            "distributes such Contribution.\n" +
            "\n" +
            "2.3. Limitations on Grant Scope\n" +
            "\n" +
            "The licenses granted in this Section 2 are the only rights granted under\n" +
            "this License. No additional rights or licenses will be implied from the\n" +
            "distribution or licensing of Covered Software under this License.\n" +
            "Notwithstanding Section 2.1(b) above, no patent license is granted by a\n" +
            "Contributor:\n" +
            "\n" +
            "(a) for any code that a Contributor has removed from Covered Software;\n" +
            "    or\n" +
            "\n" +
            "(b) for infringements caused by: (i) Your and any other third party's\n" +
            "    modifications of Covered Software, or (ii) the combination of its\n" +
            "    Contributions with other software (except as part of its Contributor\n" +
            "    Version); or\n" +
            "\n" +
            "(c) under Patent Claims infringed by Covered Software in the absence of\n" +
            "    its Contributions.\n" +
            "\n" +
            "This License does not grant any rights in the trademarks, service marks,\n" +
            "or logos of any Contributor (except as may be necessary to comply with\n" +
            "the notice requirements in Section 3.4).\n" +
            "\n" +
            "2.4. Subsequent Licenses\n" +
            "\n" +
            "No Contributor makes additional grants as a result of Your choice to\n" +
            "distribute the Covered Software under a subsequent version of this\n" +
            "License (see Section 10.2) or under the terms of a Secondary License (if\n" +
            "permitted under the terms of Section 3.3).\n" +
            "\n" +
            "2.5. Representation\n" +
            "\n" +
            "Each Contributor represents that the Contributor believes its\n" +
            "Contributions are its original creation(s) or it has sufficient rights\n" +
            "to grant the rights to its Contributions conveyed by this License.\n" +
            "\n" +
            "2.6. Fair Use\n" +
            "\n" +
            "This License is not intended to limit any rights You have under\n" +
            "applicable copyright doctrines of fair use, fair dealing, or other\n" +
            "equivalents.\n" +
            "\n" +
            "2.7. Conditions\n" +
            "\n" +
            "Sections 3.1, 3.2, 3.3, and 3.4 are conditions of the licenses granted\n" +
            "in Section 2.1.\n" +
            "\n" +
            "3. Responsibilities\n" +
            "-------------------\n" +
            "\n" +
            "3.1. Distribution of Source Form\n" +
            "\n" +
            "All distribution of Covered Software in Source Code Form, including any\n" +
            "Modifications that You create or to which You contribute, must be under\n" +
            "the terms of this License. You must inform recipients that the Source\n" +
            "Code Form of the Covered Software is governed by the terms of this\n" +
            "License, and how they can obtain a copy of this License. You may not\n" +
            "attempt to alter or restrict the recipients' rights in the Source Code\n" +
            "Form.\n" +
            "\n" +
            "3.2. Distribution of Executable Form\n" +
            "\n" +
            "If You distribute Covered Software in Executable Form then:\n" +
            "\n" +
            "(a) such Covered Software must also be made available in Source Code\n" +
            "    Form, as described in Section 3.1, and You must inform recipients of\n" +
            "    the Executable Form how they can obtain a copy of such Source Code\n" +
            "    Form by reasonable means in a timely manner, at a charge no more\n" +
            "    than the cost of distribution to the recipient; and\n" +
            "\n" +
            "(b) You may distribute such Executable Form under the terms of this\n" +
            "    License, or sublicense it under different terms, provided that the\n" +
            "    license for the Executable Form does not attempt to limit or alter\n" +
            "    the recipients' rights in the Source Code Form under this License.\n" +
            "\n" +
            "3.3. Distribution of a Larger Work\n" +
            "\n" +
            "You may create and distribute a Larger Work under terms of Your choice,\n" +
            "provided that You also comply with the requirements of this License for\n" +
            "the Covered Software. If the Larger Work is a combination of Covered\n" +
            "Software with a work governed by one or more Secondary Licenses, and the\n" +
            "Covered Software is not Incompatible With Secondary Licenses, this\n" +
            "License permits You to additionally distribute such Covered Software\n" +
            "under the terms of such Secondary License(s), so that the recipient of\n" +
            "the Larger Work may, at their option, further distribute the Covered\n" +
            "Software under the terms of either this License or such Secondary\n" +
            "License(s).\n" +
            "\n" +
            "3.4. Notices\n" +
            "\n" +
            "You may not remove or alter the substance of any license notices\n" +
            "(including copyright notices, patent notices, disclaimers of warranty,\n" +
            "or limitations of liability) contained within the Source Code Form of\n" +
            "the Covered Software, except that You may alter any license notices to\n" +
            "the extent required to remedy known factual inaccuracies.\n" +
            "\n" +
            "3.5. Application of Additional Terms\n" +
            "\n" +
            "You may choose to offer, and to charge a fee for, warranty, support,\n" +
            "indemnity or liability obligations to one or more recipients of Covered\n" +
            "Software. However, You may do so only on Your own behalf, and not on\n" +
            "behalf of any Contributor. You must make it absolutely clear that any\n" +
            "such warranty, support, indemnity, or liability obligation is offered by\n" +
            "You alone, and You hereby agree to indemnify every Contributor for any\n" +
            "liability incurred by such Contributor as a result of warranty, support,\n" +
            "indemnity or liability terms You offer. You may include additional\n" +
            "disclaimers of warranty and limitations of liability specific to any\n" +
            "jurisdiction.\n" +
            "\n" +
            "4. Inability to Comply Due to Statute or Regulation\n" +
            "---------------------------------------------------\n" +
            "\n" +
            "If it is impossible for You to comply with any of the terms of this\n" +
            "License with respect to some or all of the Covered Software due to\n" +
            "statute, judicial order, or regulation then You must: (a) comply with\n" +
            "the terms of this License to the maximum extent possible; and (b)\n" +
            "describe the limitations and the code they affect. Such description must\n" +
            "be placed in a text file included with all distributions of the Covered\n" +
            "Software under this License. Except to the extent prohibited by statute\n" +
            "or regulation, such description must be sufficiently detailed for a\n" +
            "recipient of ordinary skill to be able to understand it.\n" +
            "\n" +
            "5. Termination\n" +
            "--------------\n" +
            "\n" +
            "5.1. The rights granted under this License will terminate automatically\n" +
            "if You fail to comply with any of its terms. However, if You become\n" +
            "compliant, then the rights granted under this License from a particular\n" +
            "Contributor are reinstated (a) provisionally, unless and until such\n" +
            "Contributor explicitly and finally terminates Your grants, and (b) on an\n" +
            "ongoing basis, if such Contributor fails to notify You of the\n" +
            "non-compliance by some reasonable means prior to 60 days after You have\n" +
            "come back into compliance. Moreover, Your grants from a particular\n" +
            "Contributor are reinstated on an ongoing basis if such Contributor\n" +
            "notifies You of the non-compliance by some reasonable means, this is the\n" +
            "first time You have received notice of non-compliance with this License\n" +
            "from such Contributor, and You become compliant prior to 30 days after\n" +
            "Your receipt of the notice.\n" +
            "\n" +
            "5.2. If You initiate litigation against any entity by asserting a patent\n" +
            "infringement claim (excluding declaratory judgment actions,\n" +
            "counter-claims, and cross-claims) alleging that a Contributor Version\n" +
            "directly or indirectly infringes any patent, then the rights granted to\n" +
            "You by any and all Contributors for the Covered Software under Section\n" +
            "2.1 of this License shall terminate.\n" +
            "\n" +
            "5.3. In the event of termination under Sections 5.1 or 5.2 above, all\n" +
            "end user license agreements (excluding distributors and resellers) which\n" +
            "have been validly granted by You or Your distributors under this License\n" +
            "prior to termination shall survive termination.\n" +
            "\n" +
            "************************************************************************\n" +
            "*                                                                      *\n" +
            "*  6. Disclaimer of Warranty                                           *\n" +
            "*  -------------------------                                           *\n" +
            "*                                                                      *\n" +
            "*  Covered Software is provided under this License on an \"as is\"       *\n" +
            "*  basis, without warranty of any kind, either expressed, implied, or  *\n" +
            "*  statutory, including, without limitation, warranties that the       *\n" +
            "*  Covered Software is free of defects, merchantable, fit for a        *\n" +
            "*  particular purpose or non-infringing. The entire risk as to the     *\n" +
            "*  quality and performance of the Covered Software is with You.        *\n" +
            "*  Should any Covered Software prove defective in any respect, You     *\n" +
            "*  (not any Contributor) assume the cost of any necessary servicing,   *\n" +
            "*  repair, or correction. This disclaimer of warranty constitutes an   *\n" +
            "*  essential part of this License. No use of any Covered Software is   *\n" +
            "*  authorized under this License except under this disclaimer.         *\n" +
            "*                                                                      *\n" +
            "************************************************************************\n" +
            "\n" +
            "************************************************************************\n" +
            "*                                                                      *\n" +
            "*  7. Limitation of Liability                                          *\n" +
            "*  --------------------------                                          *\n" +
            "*                                                                      *\n" +
            "*  Under no circumstances and under no legal theory, whether tort      *\n" +
            "*  (including negligence), contract, or otherwise, shall any           *\n" +
            "*  Contributor, or anyone who distributes Covered Software as          *\n" +
            "*  permitted above, be liable to You for any direct, indirect,         *\n" +
            "*  special, incidental, or consequential damages of any character      *\n" +
            "*  including, without limitation, damages for lost profits, loss of    *\n" +
            "*  goodwill, work stoppage, computer failure or malfunction, or any    *\n" +
            "*  and all other commercial damages or losses, even if such party      *\n" +
            "*  shall have been informed of the possibility of such damages. This   *\n" +
            "*  limitation of liability shall not apply to liability for death or   *\n" +
            "*  personal injury resulting from such party's negligence to the       *\n" +
            "*  extent applicable law prohibits such limitation. Some               *\n" +
            "*  jurisdictions do not allow the exclusion or limitation of           *\n" +
            "*  incidental or consequential damages, so this exclusion and          *\n" +
            "*  limitation may not apply to You.                                    *\n" +
            "*                                                                      *\n" +
            "************************************************************************\n" +
            "\n" +
            "8. Litigation\n" +
            "-------------\n" +
            "\n" +
            "Any litigation relating to this License may be brought only in the\n" +
            "courts of a jurisdiction where the defendant maintains its principal\n" +
            "place of business and such litigation shall be governed by laws of that\n" +
            "jurisdiction, without reference to its conflict-of-law provisions.\n" +
            "Nothing in this Section shall prevent a party's ability to bring\n" +
            "cross-claims or counter-claims.\n" +
            "\n" +
            "9. Miscellaneous\n" +
            "----------------\n" +
            "\n" +
            "This License represents the complete agreement concerning the subject\n" +
            "matter hereof. If any provision of this License is held to be\n" +
            "unenforceable, such provision shall be reformed only to the extent\n" +
            "necessary to make it enforceable. Any law or regulation which provides\n" +
            "that the language of a contract shall be construed against the drafter\n" +
            "shall not be used to construe this License against a Contributor.\n" +
            "\n" +
            "10. Versions of the License\n" +
            "---------------------------\n" +
            "\n" +
            "10.1. New Versions\n" +
            "\n" +
            "Mozilla Foundation is the license steward. Except as provided in Section\n" +
            "10.3, no one other than the license steward has the right to modify or\n" +
            "publish new versions of this License. Each version will be given a\n" +
            "distinguishing version number.\n" +
            "\n" +
            "10.2. Effect of New Versions\n" +
            "\n" +
            "You may distribute the Covered Software under the terms of the version\n" +
            "of the License under which You originally received the Covered Software,\n" +
            "or under the terms of any subsequent version published by the license\n" +
            "steward.\n" +
            "\n" +
            "10.3. Modified Versions\n" +
            "\n" +
            "If you create software not governed by this License, and you want to\n" +
            "create a new license for such software, you may create and use a\n" +
            "modified version of this License if you rename the license and remove\n" +
            "any references to the name of the license steward (except to note that\n" +
            "such modified license differs from this License).\n" +
            "\n" +
            "10.4. Distributing Source Code Form that is Incompatible With Secondary\n" +
            "Licenses\n" +
            "\n" +
            "If You choose to distribute Source Code Form that is Incompatible With\n" +
            "Secondary Licenses under the terms of this version of the License, the\n" +
            "notice described in Exhibit B of this License must be attached.\n" +
            "\n" +
            "Exhibit A - Source Code Form License Notice\n" +
            "-------------------------------------------\n" +
            "\n" +
            "  This Source Code Form is subject to the terms of the Mozilla Public\n" +
            "  License, v. 2.0. If a copy of the MPL was not distributed with this\n" +
            "  file, You can obtain one at http://mozilla.org/MPL/2.0/.\n" +
            "\n" +
            "If it is not possible or desirable to put the notice in a particular\n" +
            "file, then You may include the notice in a location (such as a LICENSE\n" +
            "file in a relevant directory) where a recipient would be likely to look\n" +
            "for such a notice.\n" +
            "\n" +
            "You may add additional accurate notices of copyright ownership.\n" +
            "\n" +
            "Exhibit B - \"Incompatible With Secondary Licenses\" Notice\n" +
            "---------------------------------------------------------\n" +
            "\n" +
            "  This Source Code Form is \"Incompatible With Secondary Licenses\", as\n" +
            "defined by the Mozilla Public License, v. 2.0."},{"Android Open Source Project", "\n" +
            "                                 Apache License\n" +
            "                           Version 2.0, January 2004\n" +
            "                        http://www.apache.org/licenses/\n" +
            "\n" +
            "   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
            "\n" +
            "   1. Definitions.\n" +
            "\n" +
            "      \"License\" shall mean the terms and conditions for use, reproduction,\n" +
            "      and distribution as defined by Sections 1 through 9 of this document.\n" +
            "\n" +
            "      \"Licensor\" shall mean the copyright owner or entity authorized by\n" +
            "      the copyright owner that is granting the License.\n" +
            "\n" +
            "      \"Legal Entity\" shall mean the union of the acting entity and all\n" +
            "      other entities that control, are controlled by, or are under common\n" +
            "      control with that entity. For the purposes of this definition,\n" +
            "      \"control\" means (i) the power, direct or indirect, to cause the\n" +
            "      direction or management of such entity, whether by contract or\n" +
            "      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
            "      outstanding shares, or (iii) beneficial ownership of such entity.\n" +
            "\n" +
            "      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
            "      exercising permissions granted by this License.\n" +
            "\n" +
            "      \"Source\" form shall mean the preferred form for making modifications,\n" +
            "      including but not limited to software source code, documentation\n" +
            "      source, and configuration files.\n" +
            "\n" +
            "      \"Object\" form shall mean any form resulting from mechanical\n" +
            "      transformation or translation of a Source form, including but\n" +
            "      not limited to compiled object code, generated documentation,\n" +
            "      and conversions to other media types.\n" +
            "\n" +
            "      \"Work\" shall mean the work of authorship, whether in Source or\n" +
            "      Object form, made available under the License, as indicated by a\n" +
            "      copyright notice that is included in or attached to the work\n" +
            "      (an example is provided in the Appendix below).\n" +
            "\n" +
            "      \"Derivative Works\" shall mean any work, whether in Source or Object\n" +
            "      form, that is based on (or derived from) the Work and for which the\n" +
            "      editorial revisions, annotations, elaborations, or other modifications\n" +
            "      represent, as a whole, an original work of authorship. For the purposes\n" +
            "      of this License, Derivative Works shall not include works that remain\n" +
            "      separable from, or merely link (or bind by name) to the interfaces of,\n" +
            "      the Work and Derivative Works thereof.\n" +
            "\n" +
            "      \"Contribution\" shall mean any work of authorship, including\n" +
            "      the original version of the Work and any modifications or additions\n" +
            "      to that Work or Derivative Works thereof, that is intentionally\n" +
            "      submitted to Licensor for inclusion in the Work by the copyright owner\n" +
            "      or by an individual or Legal Entity authorized to submit on behalf of\n" +
            "      the copyright owner. For the purposes of this definition, \"submitted\"\n" +
            "      means any form of electronic, verbal, or written communication sent\n" +
            "      to the Licensor or its representatives, including but not limited to\n" +
            "      communication on electronic mailing lists, source code control systems,\n" +
            "      and issue tracking systems that are managed by, or on behalf of, the\n" +
            "      Licensor for the purpose of discussing and improving the Work, but\n" +
            "      excluding communication that is conspicuously marked or otherwise\n" +
            "      designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
            "\n" +
            "      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
            "      on behalf of whom a Contribution has been received by Licensor and\n" +
            "      subsequently incorporated within the Work.\n" +
            "\n" +
            "   2. Grant of Copyright License. Subject to the terms and conditions of\n" +
            "      this License, each Contributor hereby grants to You a perpetual,\n" +
            "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
            "      copyright license to reproduce, prepare Derivative Works of,\n" +
            "      publicly display, publicly perform, sublicense, and distribute the\n" +
            "      Work and such Derivative Works in Source or Object form.\n" +
            "\n" +
            "   3. Grant of Patent License. Subject to the terms and conditions of\n" +
            "      this License, each Contributor hereby grants to You a perpetual,\n" +
            "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
            "      (except as stated in this section) patent license to make, have made,\n" +
            "      use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
            "      where such license applies only to those patent claims licensable\n" +
            "      by such Contributor that are necessarily infringed by their\n" +
            "      Contribution(s) alone or by combination of their Contribution(s)\n" +
            "      with the Work to which such Contribution(s) was submitted. If You\n" +
            "      institute patent litigation against any entity (including a\n" +
            "      cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
            "      or a Contribution incorporated within the Work constitutes direct\n" +
            "      or contributory patent infringement, then any patent licenses\n" +
            "      granted to You under this License for that Work shall terminate\n" +
            "      as of the date such litigation is filed.\n" +
            "\n" +
            "   4. Redistribution. You may reproduce and distribute copies of the\n" +
            "      Work or Derivative Works thereof in any medium, with or without\n" +
            "      modifications, and in Source or Object form, provided that You\n" +
            "      meet the following conditions:\n" +
            "\n" +
            "      (a) You must give any other recipients of the Work or\n" +
            "          Derivative Works a copy of this License; and\n" +
            "\n" +
            "      (b) You must cause any modified files to carry prominent notices\n" +
            "          stating that You changed the files; and\n" +
            "\n" +
            "      (c) You must retain, in the Source form of any Derivative Works\n" +
            "          that You distribute, all copyright, patent, trademark, and\n" +
            "          attribution notices from the Source form of the Work,\n" +
            "          excluding those notices that do not pertain to any part of\n" +
            "          the Derivative Works; and\n" +
            "\n" +
            "      (d) If the Work includes a \"NOTICE\" text file as part of its\n" +
            "          distribution, then any Derivative Works that You distribute must\n" +
            "          include a readable copy of the attribution notices contained\n" +
            "          within such NOTICE file, excluding those notices that do not\n" +
            "          pertain to any part of the Derivative Works, in at least one\n" +
            "          of the following places: within a NOTICE text file distributed\n" +
            "          as part of the Derivative Works; within the Source form or\n" +
            "          documentation, if provided along with the Derivative Works; or,\n" +
            "          within a display generated by the Derivative Works, if and\n" +
            "          wherever such third-party notices normally appear. The contents\n" +
            "          of the NOTICE file are for informational purposes only and\n" +
            "          do not modify the License. You may add Your own attribution\n" +
            "          notices within Derivative Works that You distribute, alongside\n" +
            "          or as an addendum to the NOTICE text from the Work, provided\n" +
            "          that such additional attribution notices cannot be construed\n" +
            "          as modifying the License.\n" +
            "\n" +
            "      You may add Your own copyright statement to Your modifications and\n" +
            "      may provide additional or different license terms and conditions\n" +
            "      for use, reproduction, or distribution of Your modifications, or\n" +
            "      for any such Derivative Works as a whole, provided Your use,\n" +
            "      reproduction, and distribution of the Work otherwise complies with\n" +
            "      the conditions stated in this License.\n" +
            "\n" +
            "   5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
            "      any Contribution intentionally submitted for inclusion in the Work\n" +
            "      by You to the Licensor shall be under the terms and conditions of\n" +
            "      this License, without any additional terms or conditions.\n" +
            "      Notwithstanding the above, nothing herein shall supersede or modify\n" +
            "      the terms of any separate license agreement you may have executed\n" +
            "      with Licensor regarding such Contributions.\n" +
            "\n" +
            "   6. Trademarks. This License does not grant permission to use the trade\n" +
            "      names, trademarks, service marks, or product names of the Licensor,\n" +
            "      except as required for reasonable and customary use in describing the\n" +
            "      origin of the Work and reproducing the content of the NOTICE file.\n" +
            "\n" +
            "   7. Disclaimer of Warranty. Unless required by applicable law or\n" +
            "      agreed to in writing, Licensor provides the Work (and each\n" +
            "      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
            "      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
            "      implied, including, without limitation, any warranties or conditions\n" +
            "      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
            "      PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
            "      appropriateness of using or redistributing the Work and assume any\n" +
            "      risks associated with Your exercise of permissions under this License.\n" +
            "\n" +
            "   8. Limitation of Liability. In no event and under no legal theory,\n" +
            "      whether in tort (including negligence), contract, or otherwise,\n" +
            "      unless required by applicable law (such as deliberate and grossly\n" +
            "      negligent acts) or agreed to in writing, shall any Contributor be\n" +
            "      liable to You for damages, including any direct, indirect, special,\n" +
            "      incidental, or consequential damages of any character arising as a\n" +
            "      result of this License or out of the use or inability to use the\n" +
            "      Work (including but not limited to damages for loss of goodwill,\n" +
            "      work stoppage, computer failure or malfunction, or any and all\n" +
            "      other commercial damages or losses), even if such Contributor\n" +
            "      has been advised of the possibility of such damages.\n" +
            "\n" +
            "   9. Accepting Warranty or Additional Liability. While redistributing\n" +
            "      the Work or Derivative Works thereof, You may choose to offer,\n" +
            "      and charge a fee for, acceptance of support, warranty, indemnity,\n" +
            "      or other liability obligations and/or rights consistent with this\n" +
            "      License. However, in accepting such obligations, You may act only\n" +
            "      on Your own behalf and on Your sole responsibility, not on behalf\n" +
            "      of any other Contributor, and only if You agree to indemnify,\n" +
            "      defend, and hold each Contributor harmless for any liability\n" +
            "      incurred by, or claims asserted against, such Contributor by reason\n" +
            "      of your accepting any such warranty or additional liability.\n" +
            "\n" +
            "   END OF TERMS AND CONDITIONS\n" +
            "\n" +
            "   APPENDIX: How to apply the Apache License to your work.\n" +
            "\n" +
            "      To apply the Apache License to your work, attach the following\n" +
            "      boilerplate notice, with the fields enclosed by brackets \"[]\"\n" +
            "      replaced with your own identifying information. (Don't include\n" +
            "      the brackets!)  The text should be enclosed in the appropriate\n" +
            "      comment syntax for the file format. We also recommend that a\n" +
            "      file or class name and description of purpose be included on the\n" +
            "      same \"printed page\" as the copyright notice for easier\n" +
            "      identification within third-party archives.\n" +
            "\n" +
            "   Copyright [yyyy] [name of copyright owner]\n" +
            "\n" +
            "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "   you may not use this file except in compliance with the License.\n" +
            "   You may obtain a copy of the License at\n" +
            "\n" +
            "       http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "   Unless required by applicable law or agreed to in writing, software\n" +
            "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "   See the License for the specific language governing permissions and\n" +
            "   limitations under the License.\n"+"Material Design Library\n"+"\n" +
            "Apache License\n" +
            "                           Version 2.0, January 2004\n" +
            "                        http://www.apache.org/licenses/\n" +
            "\n" +
            "   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
            "\n" +
            "   1. Definitions.\n" +
            "\n" +
            "      \"License\" shall mean the terms and conditions for use, reproduction,\n" +
            "      and distribution as defined by Sections 1 through 9 of this document.\n" +
            "\n" +
            "      \"Licensor\" shall mean the copyright owner or entity authorized by\n" +
            "      the copyright owner that is granting the License.\n" +
            "\n" +
            "      \"Legal Entity\" shall mean the union of the acting entity and all\n" +
            "      other entities that control, are controlled by, or are under common\n" +
            "      control with that entity. For the purposes of this definition,\n" +
            "      \"control\" means (i) the power, direct or indirect, to cause the\n" +
            "      direction or management of such entity, whether by contract or\n" +
            "      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
            "      outstanding shares, or (iii) beneficial ownership of such entity.\n" +
            "\n" +
            "      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
            "      exercising permissions granted by this License.\n" +
            "\n" +
            "      \"Source\" form shall mean the preferred form for making modifications,\n" +
            "      including but not limited to software source code, documentation\n" +
            "      source, and configuration files.\n" +
            "\n" +
            "      \"Object\" form shall mean any form resulting from mechanical\n" +
            "      transformation or translation of a Source form, including but\n" +
            "      not limited to compiled object code, generated documentation,\n" +
            "      and conversions to other media types.\n" +
            "\n" +
            "      \"Work\" shall mean the work of authorship, whether in Source or\n" +
            "      Object form, made available under the License, as indicated by a\n" +
            "      copyright notice that is included in or attached to the work\n" +
            "      (an example is provided in the Appendix below).\n" +
            "\n" +
            "      \"Derivative Works\" shall mean any work, whether in Source or Object\n" +
            "      form, that is based on (or derived from) the Work and for which the\n" +
            "      editorial revisions, annotations, elaborations, or other modifications\n" +
            "      represent, as a whole, an original work of authorship. For the purposes\n" +
            "      of this License, Derivative Works shall not include works that remain\n" +
            "      separable from, or merely link (or bind by name) to the interfaces of,\n" +
            "      the Work and Derivative Works thereof.\n" +
            "\n" +
            "      \"Contribution\" shall mean any work of authorship, including\n" +
            "      the original version of the Work and any modifications or additions\n" +
            "      to that Work or Derivative Works thereof, that is intentionally\n" +
            "      submitted to Licensor for inclusion in the Work by the copyright owner\n" +
            "      or by an individual or Legal Entity authorized to submit on behalf of\n" +
            "      the copyright owner. For the purposes of this definition, \"submitted\"\n" +
            "      means any form of electronic, verbal, or written communication sent\n" +
            "      to the Licensor or its representatives, including but not limited to\n" +
            "      communication on electronic mailing lists, source code control systems,\n" +
            "      and issue tracking systems that are managed by, or on behalf of, the\n" +
            "      Licensor for the purpose of discussing and improving the Work, but\n" +
            "      excluding communication that is conspicuously marked or otherwise\n" +
            "      designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
            "\n" +
            "      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
            "      on behalf of whom a Contribution has been received by Licensor and\n" +
            "      subsequently incorporated within the Work.\n" +
            "\n" +
            "   2. Grant of Copyright License. Subject to the terms and conditions of\n" +
            "      this License, each Contributor hereby grants to You a perpetual,\n" +
            "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
            "      copyright license to reproduce, prepare Derivative Works of,\n" +
            "      publicly display, publicly perform, sublicense, and distribute the\n" +
            "      Work and such Derivative Works in Source or Object form.\n" +
            "\n" +
            "   3. Grant of Patent License. Subject to the terms and conditions of\n" +
            "      this License, each Contributor hereby grants to You a perpetual,\n" +
            "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
            "      (except as stated in this section) patent license to make, have made,\n" +
            "      use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
            "      where such license applies only to those patent claims licensable\n" +
            "      by such Contributor that are necessarily infringed by their\n" +
            "      Contribution(s) alone or by combination of their Contribution(s)\n" +
            "      with the Work to which such Contribution(s) was submitted. If You\n" +
            "      institute patent litigation against any entity (including a\n" +
            "      cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
            "      or a Contribution incorporated within the Work constitutes direct\n" +
            "      or contributory patent infringement, then any patent licenses\n" +
            "      granted to You under this License for that Work shall terminate\n" +
            "      as of the date such litigation is filed.\n" +
            "\n" +
            "   4. Redistribution. You may reproduce and distribute copies of the\n" +
            "      Work or Derivative Works thereof in any medium, with or without\n" +
            "      modifications, and in Source or Object form, provided that You\n" +
            "      meet the following conditions:\n" +
            "\n" +
            "      (a) You must give any other recipients of the Work or\n" +
            "          Derivative Works a copy of this License; and\n" +
            "\n" +
            "      (b) You must cause any modified files to carry prominent notices\n" +
            "          stating that You changed the files; and\n" +
            "\n" +
            "      (c) You must retain, in the Source form of any Derivative Works\n" +
            "          that You distribute, all copyright, patent, trademark, and\n" +
            "          attribution notices from the Source form of the Work,\n" +
            "          excluding those notices that do not pertain to any part of\n" +
            "          the Derivative Works; and\n" +
            "\n" +
            "      (d) If the Work includes a \"NOTICE\" text file as part of its\n" +
            "          distribution, then any Derivative Works that You distribute must\n" +
            "          include a readable copy of the attribution notices contained\n" +
            "          within such NOTICE file, excluding those notices that do not\n" +
            "          pertain to any part of the Derivative Works, in at least one\n" +
            "          of the following places: within a NOTICE text file distributed\n" +
            "          as part of the Derivative Works; within the Source form or\n" +
            "          documentation, if provided along with the Derivative Works; or,\n" +
            "          within a display generated by the Derivative Works, if and\n" +
            "          wherever such third-party notices normally appear. The contents\n" +
            "          of the NOTICE file are for informational purposes only and\n" +
            "          do not modify the License. You may add Your own attribution\n" +
            "          notices within Derivative Works that You distribute, alongside\n" +
            "          or as an addendum to the NOTICE text from the Work, provided\n" +
            "          that such additional attribution notices cannot be construed\n" +
            "          as modifying the License.\n" +
            "\n" +
            "      You may add Your own copyright statement to Your modifications and\n" +
            "      may provide additional or different license terms and conditions\n" +
            "      for use, reproduction, or distribution of Your modifications, or\n" +
            "      for any such Derivative Works as a whole, provided Your use,\n" +
            "      reproduction, and distribution of the Work otherwise complies with\n" +
            "      the conditions stated in this License.\n" +
            "\n" +
            "   5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
            "      any Contribution intentionally submitted for inclusion in the Work\n" +
            "      by You to the Licensor shall be under the terms and conditions of\n" +
            "      this License, without any additional terms or conditions.\n" +
            "      Notwithstanding the above, nothing herein shall supersede or modify\n" +
            "      the terms of any separate license agreement you may have executed\n" +
            "      with Licensor regarding such Contributions.\n" +
            "\n" +
            "   6. Trademarks. This License does not grant permission to use the trade\n" +
            "      names, trademarks, service marks, or product names of the Licensor,\n" +
            "      except as required for reasonable and customary use in describing the\n" +
            "      origin of the Work and reproducing the content of the NOTICE file.\n" +
            "\n" +
            "   7. Disclaimer of Warranty. Unless required by applicable law or\n" +
            "      agreed to in writing, Licensor provides the Work (and each\n" +
            "      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
            "      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
            "      implied, including, without limitation, any warranties or conditions\n" +
            "      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
            "      PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
            "      appropriateness of using or redistributing the Work and assume any\n" +
            "      risks associated with Your exercise of permissions under this License.\n" +
            "\n" +
            "   8. Limitation of Liability. In no event and under no legal theory,\n" +
            "      whether in tort (including negligence), contract, or otherwise,\n" +
            "      unless required by applicable law (such as deliberate and grossly\n" +
            "      negligent acts) or agreed to in writing, shall any Contributor be\n" +
            "      liable to You for damages, including any direct, indirect, special,\n" +
            "      incidental, or consequential damages of any character arising as a\n" +
            "      result of this License or out of the use or inability to use the\n" +
            "      Work (including but not limited to damages for loss of goodwill,\n" +
            "      work stoppage, computer failure or malfunction, or any and all\n" +
            "      other commercial damages or losses), even if such Contributor\n" +
            "      has been advised of the possibility of such damages.\n" +
            "\n" +
            "   9. Accepting Warranty or Additional Liability. While redistributing\n" +
            "      the Work or Derivative Works thereof, You may choose to offer,\n" +
            "      and charge a fee for, acceptance of support, warranty, indemnity,\n" +
            "      or other liability obligations and/or rights consistent with this\n" +
            "      License. However, in accepting such obligations, You may act only\n" +
            "      on Your own behalf and on Your sole responsibility, not on behalf\n" +
            "      of any other Contributor, and only if You agree to indemnify,\n" +
            "      defend, and hold each Contributor harmless for any liability\n" +
            "      incurred by, or claims asserted against, such Contributor by reason\n" +
            "      of your accepting any such warranty or additional liability.\n" +
            "\n" +
            "   END OF TERMS AND CONDITIONS\n" +
            "\n" +
            "   APPENDIX: How to apply the Apache License to your work.\n" +
            "\n" +
            "      To apply the Apache License to your work, attach the following\n" +
            "      boilerplate notice, with the fields enclosed by brackets \"{}\"\n" +
            "      replaced with your own identifying information. (Don't include\n" +
            "      the brackets!)  The text should be enclosed in the appropriate\n" +
            "      comment syntax for the file format. We also recommend that a\n" +
            "      file or class name and description of purpose be included on the\n" +
            "      same \"printed page\" as the copyright notice for easier\n" +
            "      identification within third-party archives.\n" +
            "\n" +
            "   Copyright {yyyy} {name of copyright owner}\n" +
            "\n" +
            "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "   you may not use this file except in compliance with the License.\n" +
            "   You may obtain a copy of the License at\n" +
            "\n" +
            "       http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "   Unless required by applicable law or agreed to in writing, software\n" +
            "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "   See the License for the specific language governing permissions and\n" +
            "limitations under the License."}};

}
